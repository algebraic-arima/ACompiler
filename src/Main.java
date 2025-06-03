package src;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import src.ASM.ASMBuilder;
import src.ASM.ASMPrinter;
import src.ASM.ASMProg;
import src.AST.ASTBuilder;
import src.AST.Prog;
import src.IR.IRBuilder;
import src.IR.IRPrinter;
import src.IR.IRProg;
import src.Optim.DCE.DCE;
import src.Optim.Jopt.Jopt;
import src.Optim.Mem2Reg.Mem2Reg;
import src.Optim.RegAlloc.RegAlloc;
import src.Semantic.*;
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
import java.util.stream.Stream;

import io.github.rctcwyvrn.blake3.Blake3;

public class Main {

    static ArrayList<File> srcFiles = new ArrayList<>();
    static ArrayList<File> asmFiles = new ArrayList<>();
    static ArrayList<ASMProg> ASMList = new ArrayList<>();

    static void getFiles(String dir) {
        Path dirPath = Paths.get(dir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    if (path.toString().endsWith(".mx")) {
                        srcFiles.add(path.toFile());
                    } else if (path.toString().endsWith(".s")){
                        asmFiles.add(path.toFile());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {

//        {
//            Blake3 hasher = Blake3.newInstance();
//            hasher.update("divf".getBytes());
//            hasher.update("shg".getBytes());
//            hasher.update("dfdg".getBytes());
//            String hexhash = hasher.hexdigest();
//            System.out.println(hexhash);
//        }
//        {
//            Blake3 hasher = Blake3.newInstance();
//            hasher.update("div".getBytes());
//            hasher.update("shg".getBytes());
//            hasher.update("dfdg".getBytes());
//            String hexhash = hasher.hexdigest();
//            System.out.println(hexhash);
//        }

        String src = "/home/limike/Git/compiler-2025/testcases/multi/t3/";
        getFiles(src);

        for (File fileIn : srcFiles) {
            try {
                InputStream in = new FileInputStream(fileIn);
                Lex lexer = new Lex(CharStreams.fromStream(in));
                lexer.removeErrorListeners();
                lexer.addErrorListener(new MxErrorListener());
                Mx parser = new Mx(new CommonTokenStream(lexer));
                parser.removeErrorListeners();
                parser.addErrorListener(new MxErrorListener());
                Mx.ProgContext parseTreeRoot = parser.prog();
                ASTBuilder AST = new ASTBuilder();
                Prog ASTRoot = (Prog) AST.visit(parseTreeRoot);
                GlobalScope gScope = new GlobalScope();
                new SymbolCollector(gScope).visit(ASTRoot);
                SemanticChecker sc = new SemanticChecker(gScope);
                sc.visit(ASTRoot);
                ASTRoot.setHash();



                IRBuilder irBuilder = new IRBuilder(gScope);
                IRProg irProg = irBuilder.build(ASTRoot);
                irProg.reformat();
//                if (printIR) {
//                    IRPrinter irPrinter = new IRPrinter(irProg);
//                    if (fileOutIR) {
//                        FileOutputStream fileOutputStream = new FileOutputStream("/home/limike/Git/compiler-2024/ir.ll");
//                        PrintStream printStream = new PrintStream(fileOutputStream);
//                        System.setOut(printStream);
//                    } else {
//                        PrintStream consolePrintStream = new PrintStream(new FileOutputStream(FileDescriptor.out));
//                        System.setOut(consolePrintStream);
//                    }
//                    irPrinter.print();
//                }

                IRPrinter irPrinter = new IRPrinter(irBuilder.irProg);
                String fileOutIR = fileIn.getAbsolutePath().replaceAll(".mx", ".ll");
                FileOutputStream fileOutputStreamIR = new FileOutputStream(fileOutIR);
                PrintStream printStreamIR = new PrintStream(fileOutputStreamIR);
                System.setOut(printStreamIR);
                irPrinter.print();

                Mem2Reg m2r = new Mem2Reg(irProg);
                DCE dce = new DCE(irProg);
                RegAlloc ra = new RegAlloc(irProg);
                ASMBuilder asmBuilder = new ASMBuilder(irBuilder.irProg, false);
                ASMProg asmProg = asmBuilder.asmProg;
                Jopt j = new Jopt(asmProg);

                ASMList.add(asmProg);

                ASMPrinter asmPrinter = new ASMPrinter(asmBuilder.asmProg);
                String fileOutASM = fileIn.getAbsolutePath().replaceAll(".mx", ".s");
                FileOutputStream fileOutputStreamASM = new FileOutputStream(fileOutASM);
                PrintStream printStreamASM = new PrintStream(fileOutputStreamASM);
                System.setOut(printStreamASM);
                asmPrinter.print();
            } catch (error e) {
                System.err.println("In " + fileIn + ": (" + e.pos.row + ":" + e.pos.column + ") " + e.message);
                System.exit(1); // if judge
            }
        }

        System.exit(0);

    }
}
