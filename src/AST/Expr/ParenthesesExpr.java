package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.Entity.Register;
import src.utils.pos.Position;

public class ParenthesesExpr extends Expr {

    public Expr expr;
    public Register addr;

    public ParenthesesExpr(Position p, Expr e) {
        super(p);
        expr = e;
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("ParenthesesExpr", type, expr);
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
