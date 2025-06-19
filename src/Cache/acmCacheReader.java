package src.Cache;

import src.AST.Def.FuncDef;
import src.cAST.Def.CDef;
import src.cAST.Def.FuncCDef;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class acmCacheReader {
    Diff diff;
    String asmcacheDir = "/home/limike/.mcache/";
    ArrayList<List<String>> asmFromCache = new ArrayList<>();

    public acmCacheReader(File f, Diff diff) throws IOException {
        this.diff = diff;
        asmcacheDir = asmcacheDir + f.getName() + "/asm/";
    }

    public void readASMCache() throws IOException {
        for (Map.Entry<String, FuncDef> entry : diff.stable.entrySet()) {
            assert !entry.getValue().isCompile();
            File f = new File(asmcacheDir + entry.getKey());
            if(!f.exists()) {
                throw new RuntimeException("no such cache file!");
            }
            // read the first line of the file, which is the hash
            List<String> lines = Files.readAllLines(f.toPath());
            String hash = lines.getFirst().trim();
            assert hash.length() == 64;
            lines.removeFirst();
            if(hash.equals(entry.getValue().hash)) {
                asmFromCache.add(lines);
            } else {
                entry.getValue().setCompile();
            }
        }
    }

    public void outputCache() throws IOException {
        for (List<String> lines : asmFromCache) {
            for (String line : lines) {
                System.out.println(line);
            }
        }
    }
}
