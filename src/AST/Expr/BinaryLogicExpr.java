package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

public class BinaryLogicExpr extends Expr {

    public enum BLogicOp {
        AND, OR
    }

    public Expr lhs, rhs;
    public BLogicOp op;

    public BinaryLogicExpr(Position p, Expr lhs, Expr rhs, String op) {
        super(p);
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
    public void setHash() {
        hasher.update("BinaryLogicExpr", type, lhs, op.toString(), rhs);
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
