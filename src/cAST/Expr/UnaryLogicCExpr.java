package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class UnaryLogicCExpr extends CExpr {

    public enum ULogicOp {
        NOT
    }

    public CExpr CExpr;
    public ULogicOp op;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr e) {
            if (CExpr == null) {
                CExpr = e;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        throw new RuntimeException("AST type not match!");
    }
}
