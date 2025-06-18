package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class UnaryArithCExpr extends CExpr {

    public enum UArithOp {
        NEG, BNOT, LINC, RINC, LDEC, RDEC
    }

    public CExpr CExpr;
    public UArithOp op;

    public UnaryArithCExpr(CExpr e, String o, boolean isLeft) {
        CExpr = e;
        switch (o) {
            case "-":
                op = UArithOp.NEG;
                break;
            case "~":
                op = UArithOp.BNOT;
                break;
            case "++":
                op = isLeft ? UArithOp.LINC : UArithOp.RINC;
                break;
            case "--":
                op = isLeft ? UArithOp.LDEC : UArithOp.RDEC;
                break;
        }
    }

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
