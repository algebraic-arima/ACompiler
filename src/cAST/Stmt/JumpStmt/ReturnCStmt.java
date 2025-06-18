package src.cAST.Stmt.JumpStmt;

import src.cAST.BaseCASTNode;
import src.cAST.Expr.CExpr;
import src.cAST.Stmt.CStmt;

public class ReturnCStmt extends CStmt {
    public CExpr retCExpr;

    @Override
    public void addChild(BaseCASTNode c) {
        if(retCExpr == null && c instanceof CExpr e){
            retCExpr = e;
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
