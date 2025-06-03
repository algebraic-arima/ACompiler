package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

public class ThisPtrExpr extends Expr {

    public String className;

    public ThisPtrExpr(Position p) {
        super(p);
    }

    @Override
    public void setHash() {
        hasher.update("ThisPtrExpr");
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
