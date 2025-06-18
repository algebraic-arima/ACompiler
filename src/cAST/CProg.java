package src.cAST;

import src.cAST.Def.CDef;
import src.cAST.Def.FuncCDef;

import java.util.HashMap;

public class CProg extends BaseCASTNode {
    public HashMap<String, CDef> defs = new HashMap<>();

    @Override
    public void addChild(BaseCASTNode c) {
        if(c instanceof CDef cdef){
            defs.put(cdef.hash, cdef);
        }
        throw new RuntimeException("AST type not match!");
    }

    public CDef findCache(String hash){
        return defs.getOrDefault(hash, null);
    }

}