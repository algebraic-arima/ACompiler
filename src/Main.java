package src;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import src.ASM.ASMBuilder;
import src.ASM.ASMPrinter;
import src.ASM.ASMProg;
import src.AST.ASTBuilder;
import src.AST.Prog;
import src.Cache.*;
import src.IR.IRBuilder;
import src.IR.IRPrinter;
import src.IR.IRProg;
import src.Linker.Linker;
import src.Optim.DCE.DCE;
import src.Optim.Jopt.Jopt;
import src.Optim.Mem2Reg.Mem2Reg;
import src.Optim.RegAlloc.RegAlloc;
import src.Semantic.*;
import src.cAST.CProg;
import src.parser.Lex;
import src.parser.Mx;
import src.utils.MxErrorListener;
import src.utils.Scope.GlobalScope;
import src.utils.error.error;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    static ArrayList<File> srcFiles = new ArrayList<>();
    static final ReentrantLock globalLock = new ReentrantLock();

    static void getFiles(String dir) {
        Path dirPath = Paths.get(dir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    if (path.toString().endsWith(".mx")) {
                        srcFiles.add(path.toFile());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String src = "/home/limike/Git/compiler-2025/testcases/multi/t2/";
        getFiles(src);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (File fileIn : srcFiles) {
            executor.submit(() -> {
                globalLock.lock(); // Acquire the global lock for this file processing
                try {
                    processFile(fileIn);
                } catch (Exception e) {
                    System.err.println("Error processing file " + fileIn + ": " + e.getMessage());
                    e.printStackTrace();
                    System.exit(1);
                } finally {
                    globalLock.unlock(); // Release the lock when done
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Thread pool interrupted: " + e.getMessage());
            System.exit(1);
        }

        Linker l = new Linker(srcFiles, src + "code.s");

        System.exit(0);
    }

    public static void processFile(File fileIn) {
        try {
            String fileName = fileIn.getName();
            fileName = fileName.substring(0, fileName.length() - 3);
            InputStream in = new FileInputStream(fileIn);
            Lex lexer = new Lex(CharStreams.fromStream(in));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MxErrorListener());
            Mx parser = new Mx(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new MxErrorListener());
            Mx.ProgContext parseTreeRoot = parser.prog();
            in.close();

            ASTBuilder AST = new ASTBuilder();
            Prog ASTRoot = (Prog) AST.visit(parseTreeRoot);
            ASTRoot.setHash();
            GlobalScope gScope = new GlobalScope();
            new SymbolCollector(gScope).visit(ASTRoot);
            SemanticChecker sc = new SemanticChecker(gScope);
            sc.visit(ASTRoot);

            // cache the ast
            astCacheManager cm = new astCacheManager(fileIn, ASTRoot);
            astCacheReader cr = new astCacheReader(fileIn);
            acmCacheReader acmCache = null;
            if (cr.hasCache()) {
                CProg cProg = cr.cProg;
                Diff diff = new Diff(ASTRoot, cProg);
                acmCache = new acmCacheReader(fileIn, diff);
                acmCache.readASMCache();
            }

            IRBuilder irBuilder = new IRBuilder(gScope, fileName);
            IRProg irProg = irBuilder.build(ASTRoot);
            irProg.reformat();
            IRPrinter irPrinter = new IRPrinter(irBuilder.irProg);

            PrintStream originalOut = System.out;
            String fileOutIR = fileIn.getAbsolutePath().replaceAll(".mx", ".ll");
            FileOutputStream fileOutputStreamIR = new FileOutputStream(fileOutIR);
            try (PrintStream printStreamIR = new PrintStream(fileOutputStreamIR)) {
                System.setOut(printStreamIR);
                irPrinter.print();
            } finally {
                System.setOut(originalOut);
            }
            fileOutputStreamIR.close();

            Mem2Reg m2r = new Mem2Reg(irProg);
            DCE dce = new DCE(irProg);
            RegAlloc ra = new RegAlloc(irProg);
            ASMBuilder asmBuilder = new ASMBuilder(irBuilder.irProg, false, fileName);
            ASMProg ASMProg = asmBuilder.asmProg;
            Jopt j = new Jopt(ASMProg);

            ASMPrinter asmPrinter = new ASMPrinter(ASMProg);

            originalOut = System.out;
            String fileOutASM = fileIn.getAbsolutePath().replaceAll(".mx", ".s");
            FileOutputStream fileOutputStreamASM = new FileOutputStream(fileOutASM);
            try (PrintStream printStreamASM = new PrintStream(fileOutputStreamASM)) {
                System.setOut(printStreamASM);
                if (acmCache != null) {
                    acmCache.outputCache();
                }
                asmPrinter.print();
            } finally {
                System.setOut(originalOut);
            }
            fileOutputStreamASM.close();

            // cache the asm
            cm.writeCache();
            asmCacheManager asmCache = new asmCacheManager(fileIn, ASMProg);

        } catch (error e) {
            System.err.println("In " + fileIn + ": (" + e.pos.row + ":" + e.pos.column + ") " + e.message);
            System.exit(1); // if judge
        } catch (Exception exc) {
            System.err.println(exc);
            System.exit(1);
        }
    }
}
