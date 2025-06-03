package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

import java.util.ArrayList;

public class ArrayLiteralExpr extends Expr {
    public ArrayList<Expr> elements;

    public ArrayLiteralExpr(Position p) {
        super(p);
        elements = new ArrayList<>();
    }

    @Override
    public void setHash() {
        hasher.update("ArrayLiteralExpr", type);
        elements.forEach(d -> hasher.update(d));
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
