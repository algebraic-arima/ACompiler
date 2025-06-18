package src.cAST.Stmt;

import src.cAST.BaseCASTNode;
import src.cAST.Expr.CExpr;

public class ExprCStmt extends CStmt {
    public CExpr CExpr;

    @Override
    public void addChild(BaseCASTNode c) {
        if(CExpr == null && c instanceof CExpr e){
            CExpr = e;
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
