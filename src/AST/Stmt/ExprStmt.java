package src.AST.Stmt;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.AST.Expr.Expr;
import src.utils.pos.Position;
import src.AST.BlakeHasher;

public class ExprStmt extends Stmt {
    public Expr expr;

    public ExprStmt(Position p, Expr e) {
        super(p);
        expr = e;
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("ExprStmt", expr);
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
