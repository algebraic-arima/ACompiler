package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

public class FuncCallExpr extends Expr {
    public String funcName;
    public RowExpr args;

    public FuncCallExpr(Position p, String name) {
        super(p);
        funcName = name;
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
        hasher.update("FuncCallExpr", type, funcName, args);
        hash = hasher.hexdigest();
    }
}
