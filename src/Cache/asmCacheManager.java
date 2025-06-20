package src.Cache;

import src.ASM.ASMProg;
import src.AST.Def.Constructor;
import src.AST.Def.FuncDef;
import src.AST.Def.VarDef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class asmCacheManager {

    String cacheDir = "/home/limike/.mcache/";

    public asmCacheManager(File f, ASMProg prog) throws IOException {
        for (var func : prog.funcDefs) {
            if(func.fd == null) continue;
            FileOutputStream c = new FileOutputStream(cacheDir + func.name);
            PrintStream o = System.out;
            try (PrintStream ps = new PrintStream(c)) {
                System.setOut(ps);
                System.out.println(func.fd.hash);
                if (func.fd instanceof Constructor con) {
                    System.out.println(con.funcBody.hash);
                } else if (func.fd instanceof FuncDef funcDef) {
                    System.out.println(funcDef.funcBody.hash);
                } else if (func.fd instanceof VarDef varDef) {
                    System.out.println(varDef.hash);
                } else {
                    System.out.println("nullnullnullnullnullnullnullnullnullnullnullnullnullnullnullnull");
                }
                func.print();
            } finally {
                System.setOut(o);
            }
            c.close();
        }
    }
}
