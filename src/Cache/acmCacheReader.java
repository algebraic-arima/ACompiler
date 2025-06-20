package src.Cache;

import src.ASM.ASMInst.LI;
import src.AST.Def.FuncDef;
import src.IR.IRPrinter;
import src.cAST.Def.CDef;
import src.cAST.Def.FuncCDef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class acmCacheReader {
    Diff diff;
    String asmcacheDir = "/home/limike/.mcache/";
    ArrayList<List<String>> asmFromCache = new ArrayList<>();
    HashMap<String, String> funcBodyHash = new HashMap<>();
    HashMap<String, List<String>> rewriteCache = new HashMap<>();

    public acmCacheReader(File f, Diff diff) throws IOException {
        this.diff = diff;
    }

    public void collectFuncbodyHash() throws IOException {
        File dir = new File(asmcacheDir);
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile() && !file.getName().endsWith(".ast.cache")) {
                List<String> lines = Files.readAllLines(file.toPath());
                if (lines.size() > 1) {
                    String hash = lines.get(1).trim();
                    funcBodyHash.put(file.getName(), hash);
                }
            }
        }
    }

    public void readASMCache() throws IOException {
        for (Map.Entry<String, FuncDef> entry : diff.stable.entrySet()) {
            assert !entry.getValue().isCompile();
            File f = new File(asmcacheDir + entry.getKey());
            if (!f.exists()) {
                throw new RuntimeException("no such cache file!");
            }
            // read the first line of the file, which is the hash
            List<String> lines = Files.readAllLines(f.toPath());
            String hash = lines.getFirst().trim();
            assert hash.length() == 64;
            lines.removeFirst();
            lines.removeFirst();
            if (hash.equals(entry.getValue().hash)) {
                asmFromCache.add(lines);
            } else {
                entry.getValue().setCompile();
            }
        }
        for (Map.Entry<FuncDef, String> entry : diff.renameMap.entrySet()) {
            FuncDef funcDef = entry.getKey();
            String name = entry.getValue();
            String originalName = diff.originalName.get(funcDef);
            File f = new File(asmcacheDir + name);
            if (!f.exists()) {
                throw new RuntimeException("no such cache file!");
            }
            // read the first line of the file, which is the hash
            List<String> lines = Files.readAllLines(f.toPath());
            String outHash = lines.getFirst().trim();
            lines.removeFirst();
            String hash = lines.getFirst().trim();
            assert hash.length() == 64;
            lines.removeFirst();
            lines.set(0, "  .globl " + originalName);
            lines.set(1, "  .type " + originalName + ", @function");
            lines.set(2, originalName + ":");
            if (hash.equals(funcDef.funcBody.hash)) {
                asmFromCache.add(lines);
            } else {
                funcDef.setCompile();
            }
            ArrayList<String> n = new ArrayList<>();
            n.add(outHash);
            n.add(hash);
            n.add("  .globl " + originalName);
            n.add("  .type " + originalName + ", @function");
            n.add(originalName + ":");
            n.addAll(lines);
            rewriteCache.put(originalName, n);
        }
    }

    public void outputCache() throws IOException {
        System.out.println("  .section .text");
        for (List<String> lines : asmFromCache) {
            for (String line : lines) {
                System.out.println(line);
            }
        }
        for (Map.Entry<String, List<String>> entry : rewriteCache.entrySet()) {
            String name = asmcacheDir + entry.getKey();
            List<String> content = entry.getValue();
            PrintStream originalOut = System.out;
            FileOutputStream fo = new FileOutputStream(name);
            try (PrintStream pps = new PrintStream(fo)) {
                System.setOut(pps);
                for (String line : content) {
                    System.out.println(line);
                }
            } finally {
                System.setOut(originalOut);
            }
            fo.close();
        }
    }
}
