package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class TernaryBranchCExpr extends CExpr {
    public CExpr cond, trueBranch, falseBranch;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr e) {
            if (cond == null) {
                cond = e;
                return;
            }
            if (trueBranch == null) {
                trueBranch = e;
                return;
            }
            if (falseBranch == null) {
                falseBranch = e;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        throw new RuntimeException("AST type not match!");
    }
}
