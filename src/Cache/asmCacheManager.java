package src.Cache;

import src.ASM.ASMProg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class asmCacheManager {

    static String cacheDir = "/home/limike/.mcache/asm/";

    public asmCacheManager(ASMProg prog) throws IOException {
        File file = new File(cacheDir);
        if(!file.exists() || !file.isDirectory()){
            file.mkdir();
        }
        for (var func : prog.funcDefs) {
            File cf = new File(cacheDir + func.name);
            if(!file.exists()){
                cf.createNewFile();
            }
            FileOutputStream c = new FileOutputStream(cf);
            PrintStream ps = new PrintStream(c);
            System.setOut(ps);
            System.out.println(func.hash);
            func.print();
            ps.close();
            c.close();
        }
    }
}
