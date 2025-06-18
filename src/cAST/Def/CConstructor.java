package src.cAST.Def;

import src.cAST.BaseCASTNode;
import src.cAST.Stmt.BlockCStmt;

public class CConstructor extends CDef {
    public BlockCStmt funcBody;
    public String className;

    @Override
    public void addChild(BaseCASTNode c) {
        if(funcBody == null && c instanceof BlockCStmt b){
            funcBody = b;
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
