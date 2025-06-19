package src.Cache;

import src.ASM.ASMProg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class asmCacheManager {

    String cacheDir = "/home/limike/.mcache/";

    public asmCacheManager(File f, ASMProg prog) throws IOException {
        cacheDir = cacheDir + f.getName() + "/asm/";
        File file = new File(cacheDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
        }
        for (var func : prog.funcDefs) {
            FileOutputStream c = new FileOutputStream(cacheDir + func.name);
            PrintStream o = System.out;
            try (PrintStream ps = new PrintStream(c)) {
                System.setOut(ps);
                System.out.println(func.hash);
                func.print();
            } finally {
                System.setOut(o);
            }
            c.close();
        }
    }
}
