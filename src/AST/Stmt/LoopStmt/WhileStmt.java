package src.AST.Stmt.LoopStmt;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.AST.Expr.Expr;
import src.AST.Stmt.Stmt;
import src.utils.pos.Position;
import src.AST.BlakeHasher;

public class WhileStmt extends Stmt {
    public Expr cond;
    public Stmt body;

    public WhileStmt(Position p) {
        super(p);
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("WhileStmt", cond, body);
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
