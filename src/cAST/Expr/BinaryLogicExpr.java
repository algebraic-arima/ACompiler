package src.cAST.Expr;

public class BinaryLogicExpr extends Expr {

    public enum BLogicOp {
        AND, OR
    }

    public Expr lhs, rhs;
    public BLogicOp op;

    public BinaryLogicExpr(Expr lhs, Expr rhs, String op) {
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
}
