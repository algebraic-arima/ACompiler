package src.cAST.Stmt.LoopStmt;

import src.cAST.BaseCASTNode;
import src.cAST.Expr.CExpr;
import src.cAST.Stmt.CStmt;

public class ForCStmt extends CStmt {
    public CExpr cond, update;
    public CStmt init, body;

    @Override
    public void addChild(BaseCASTNode c) {
        if (init == null) {
            if (c instanceof CStmt s) {
                init = s;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        if (update == null) {
            if (c instanceof CExpr e) {
                update = e;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        if (cond == null) {
            if (c instanceof CExpr e) {
                cond = e;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        if (body == null) {
            if (c instanceof CStmt s) {
                body = s;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        throw new RuntimeException("AST type not match!");
    }

}
