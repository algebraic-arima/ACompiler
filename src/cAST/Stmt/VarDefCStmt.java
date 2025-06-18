package src.cAST.Stmt;

import src.cAST.BaseCASTNode;
import src.cAST.Def.VarCDef;

public class VarDefCStmt extends CStmt {
    public VarCDef varDef;

    @Override
    public void addChild(BaseCASTNode c) {
        if(varDef == null && c instanceof VarCDef vd){
            varDef = vd;
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
