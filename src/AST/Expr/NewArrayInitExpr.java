package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

public class NewArrayInitExpr extends Expr{
    public ArrayLiteralExpr init;

    public NewArrayInitExpr(Position p) {
        super(p);
    }

    @Override
    public void accept(__ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("NewArrayInitExpr", type, init);
        hash = hasher.hexdigest();
    }
}
