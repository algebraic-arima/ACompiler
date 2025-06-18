package src.cAST.Stmt.JumpStmt;

import src.cAST.BaseCASTNode;
import src.cAST.Expr.CExpr;
import src.cAST.Stmt.CStmt;

public class IfCStmt extends CStmt {
    public CExpr condition;
    public CStmt trueCStmt, falseCStmt;

    @Override
    public void addChild(BaseCASTNode c) {
        if (condition == null) {
            if (c instanceof CExpr e) {
                condition = e;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        if (trueCStmt == null) {
            if (c instanceof CStmt s) {
                trueCStmt = s;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        if (falseCStmt == null) {
            if (c instanceof CStmt s) {
                falseCStmt = s;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        throw new RuntimeException("AST type not match!");
    }

}
