package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

public class NullExpr extends Expr{

    public NullExpr(Position p) {
        super(p);
        type = null;
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("NullExpr", type);
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
