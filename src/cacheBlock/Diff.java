package src.cacheBlock;

import src.AST.Def.ClassDef;
import src.AST.Def.Def;
import src.AST.Def.FuncDef;
import src.AST.Prog;
import src.cAST.CProg;
import src.cAST.Def.CDef;
import src.cAST.Def.ClassCDef;
import src.cAST.Def.FuncCDef;

import java.util.ArrayList;
import java.util.HashMap;

public class Diff {
    Prog curProg;
    CProg cacheProg;
    public HashMap<String, FuncDef> stable = new HashMap<>();

    public Diff(Prog cur, CProg cache) {
        this.curProg = cur;
        this.cacheProg = cache;
    }

    public void funcHit() {
        for (Def d : curProg.defs) {
            if (d instanceof FuncDef funcDef) {
                CDef def = cacheProg.findCache(funcDef.hash);
                if (def != null) {
                    if (def instanceof FuncCDef) {
                        stable.put(funcDef.hash, funcDef);
                    } else {
                        throw new RuntimeException("Cache function definition type mismatch!");
                    }
                }
            } else if (d instanceof ClassDef classDef) {
                CDef def = cacheProg.findCache(classDef.hash);
                if (def != null) {
                    if(def instanceof ClassCDef classCDef) {
                        for (FuncDef funcDef : classDef.classFunc){
                            stable.put(funcDef.hash, funcDef);
                        }
                    } else {
                        throw new RuntimeException("Cache class definition type mismatch!");
                    }
                } else {
                    // compare the methods

                }

            }
        }
    }
}
