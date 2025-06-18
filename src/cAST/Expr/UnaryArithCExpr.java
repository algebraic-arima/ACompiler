package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class UnaryArithCExpr extends CExpr {

    public enum UArithOp {
        NEG, BNOT, LINC, RINC, LDEC, RDEC
    }

    public CExpr CExpr;
    public UArithOp op;

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
