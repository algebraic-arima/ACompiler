package src;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import src.ASM.ASMBuilder;
import src.ASM.ASMPrinter;
import src.ASM.ASMProg;
import src.AST.ASTBuilder;
import src.AST.Prog;
import src.Cache.Diff;
import src.IR.IRBuilder;
import src.IR.IRPrinter;
import src.IR.IRProg;
import src.Optim.DCE.DCE;
import src.Optim.Jopt.Jopt;
import src.Optim.Mem2Reg.Mem2Reg;
import src.Optim.RegAlloc.RegAlloc;
import src.Semantic.*;
import src.Cache.asmCacheManager;
import src.Cache.astCacheManager;
import src.Cache.cacheReader;
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
                    } else if (path.toString().endsWith(".s")) {
                        asmFiles.add(path.toFile());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {

        String src = "/home/limike/Git/compiler-2025/testcases/multi/t3/";
        String cache = "/home/limike/.mcache/";
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
                ASTRoot.setHash();
                GlobalScope gScope = new GlobalScope();
                new SymbolCollector(gScope).visit(ASTRoot);
                SemanticChecker sc = new SemanticChecker(gScope);
                sc.visit(ASTRoot);

                // cache the ast
                astCacheManager cm = new astCacheManager(ASTRoot);
                cacheReader cr = new cacheReader();
                if(cr.hasCache()){
                    CProg cProg = cr.cProg;
                    Diff diff = new Diff(ASTRoot, cProg);
                }

                IRBuilder irBuilder = new IRBuilder(gScope);
                IRProg irProg = irBuilder.build(ASTRoot);
                irProg.reformat();

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
                ASMProg ASMProg = asmBuilder.asmProg;
                Jopt j = new Jopt(ASMProg);

                ASMList.add(ASMProg);

                ASMPrinter asmPrinter = new ASMPrinter(ASMProg);
                String fileOutASM = fileIn.getAbsolutePath().replaceAll(".mx", ".s");
                FileOutputStream fileOutputStreamASM = new FileOutputStream(fileOutASM);
                PrintStream printStreamASM = new PrintStream(fileOutputStreamASM);
                System.setOut(printStreamASM);
                asmPrinter.print();

                // cache the asm
                cm.writeCache();
                asmCacheManager asmCache = new asmCacheManager(ASMProg);

            } catch (error e) {
                System.err.println("In " + fileIn + ": (" + e.pos.row + ":" + e.pos.column + ") " + e.message);
                System.exit(1); // if judge
            }
        }

        System.exit(0);

    }
}
