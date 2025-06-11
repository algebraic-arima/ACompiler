package src.cAST.Expr;

public class UnaryLogicExpr extends Expr {

    public enum ULogicOp {
        NOT
    }

    public Expr expr;
    public ULogicOp op;

    public UnaryLogicExpr(Expr e) {
        expr = e;
        op = ULogicOp.NOT;
    }
}
