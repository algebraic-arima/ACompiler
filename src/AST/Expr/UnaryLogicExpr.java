package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

public class UnaryLogicExpr extends Expr{

    public enum ULogicOp {
        NOT
    }

    public Expr expr;
    public ULogicOp op;

    public UnaryLogicExpr(Position p, Expr e) {
        super(p);
        expr = e;
        op = ULogicOp.NOT;
    }

    @Override
    public void setHash() {
        hasher.update("UnaryLogicExpr", op.toString(), expr);
        hash = hasher.hexdigest();
    }

    @Override
    public void accept(__ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visit(this);
    }
}
