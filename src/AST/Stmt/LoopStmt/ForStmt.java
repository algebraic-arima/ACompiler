package src.AST.Stmt.LoopStmt;

import io.github.rctcwyvrn.blake3.Blake3;
import src.AST.__ASTVisitor;
import src.AST.Expr.Expr;
import src.AST.Stmt.Stmt;
import src.utils.pos.Position;
import src.AST.BlakeHasher;

public class ForStmt extends Stmt {

    public Expr cond, update;
    public Stmt init, body;

    public ForStmt(Position p) {
        super(p);
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("ForStmt", cond, update, init, body);
        hash = hasher.hexdigest();
    }

    @Override
    public void accept(__ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public <T> T accept(src.AST.ASTVisitor<T> v) {
        return v.visit(this);
    }
}
