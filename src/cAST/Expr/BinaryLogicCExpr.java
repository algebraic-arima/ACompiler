package src.cAST.Expr;
import src.cAST.BaseCASTNode;

public class BinaryLogicCExpr extends CExpr {

    public enum BLogicOp {
        AND, OR
    }

    public CExpr lhs, rhs;
    public BLogicOp op;

    public BinaryLogicCExpr(CExpr lhs, CExpr rhs, String op) {
        this.lhs = lhs;
        this.rhs = rhs;
        switch (op) {
            case "&&":
                this.op = BLogicOp.AND;
                break;
            case "||":
                this.op = BLogicOp.OR;
                break;
        }
    }

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr) {
            if (lhs == null) {
                lhs = (CExpr) c;
            } else if (rhs == null) {
                rhs = (CExpr) c;
            } else {
                throw new RuntimeException("BinaryLogicExpr can only have two children: lhs and rhs");
            }
        } else {
            throw new RuntimeException("BinaryLogicExpr can only have Expr children");
        }
    }
}
