package src.cAST;

import src.AST.BaseASTNode;
import src.AST.Def.FuncDef;
import src.cAST.Def.CDef;
import src.cAST.Def.ClassCDef;
import src.cAST.Def.FuncCDef;

import java.util.ArrayList;
import java.util.HashMap;

public class CProg extends BaseCASTNode {
    public ArrayList<CDef> defs = new ArrayList<>();

    public HashMap<String, CDef> defsMap = new HashMap<>();
    public HashMap<String, CDef> hashMap = new HashMap<>();

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CDef cdef) {
            defs.add(cdef);
            return;
        }
        throw new RuntimeException("AST type not match!");
    }

    public void collectFuncHash() {
        for (CDef def : defs) {
            if (def instanceof FuncCDef funcCDef) {
                defsMap.put(funcCDef.funcName, funcCDef);
                hashMap.put(funcCDef.hash, funcCDef);
            } else if (def instanceof ClassCDef classCDef) {
                defsMap.put(classCDef.className, classCDef);
                hashMap.put(classCDef.hash, classCDef);
                for (FuncCDef funcCDef : classCDef.classFunc) {
                    defsMap.put(classCDef.className + "::" + funcCDef.funcName, funcCDef);
                    hashMap.put(funcCDef.hash, funcCDef);
                }
            } else {
                throw new RuntimeException("Cache definition type mismatch!");
            }
        }
    }

    public CDef findCachebyHash(String hash) {
        return hashMap.getOrDefault(hash, null);
    }

    public CDef findCachebyName(String name) {
        return defsMap.getOrDefault(name, null);
    }
}