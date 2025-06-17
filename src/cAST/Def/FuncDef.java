package src.cAST.Def;

import src.cAST.BaseCASTNode;
import src.cAST.Stmt.BlockStmt;

public class FuncDef extends Def {
    public BlockStmt funcBody;

    @Override
    void addChild(BaseCASTNode c) {
        if(funcBody == null && c instanceof BlockStmt b){
            funcBody = b;
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}