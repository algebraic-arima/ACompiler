package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;
import src.utils.type.Type;

public class NewTypeExpr extends Expr{

    public NewTypeExpr(Position p, Type t) {
        super(p);
        type = t;
    }

    @Override
    public void setHash() {
        hasher.update("NewTypeExpr", type);
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
