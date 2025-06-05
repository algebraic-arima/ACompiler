package src.AST.Stmt.JumpStmt;

import io.github.rctcwyvrn.blake3.Blake3;
import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.AST.Expr.Expr;
import src.AST.Stmt.Stmt;
import src.utils.pos.Position;
import src.AST.BlakeHasher;

public class IfStmt extends Stmt {
    public Expr condition;
    public Stmt trueStmt, falseStmt;
    BlakeHasher hasher = new BlakeHasher();

    public IfStmt(Position p) {
        super(p);
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("IfStmt", condition, trueStmt, falseStmt);
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
