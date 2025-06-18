package src.cAST.Stmt.LoopStmt;

import src.cAST.BaseCASTNode;
import src.cAST.Expr.CExpr;
import src.cAST.Stmt.CStmt;

public class WhileCStmt extends CStmt {
    public CExpr cond;
    public CStmt body;

    @Override
    public void addChild(BaseCASTNode c) {

    }
}
