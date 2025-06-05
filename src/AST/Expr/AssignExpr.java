package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

public class AssignExpr extends Expr {
    public Expr var;
    public Expr value;

    public AssignExpr(Position p, Expr v, Expr va) {
        super(p);
        var = v;
        value = va;
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("AssignExpr", type, var, value);
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
