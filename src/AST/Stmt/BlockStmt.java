package src.AST.Stmt;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;
import src.AST.BlakeHasher;

import java.util.ArrayList;

public class BlockStmt extends Stmt {
    public ArrayList<Stmt> stmts;

    public BlockStmt(Position p){
        super(p);
        stmts = new ArrayList<>();
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
        hasher.update("BlockStmt");
        for (Stmt stmt : stmts) {
            hasher.update(stmt);
        }
        hash = hasher.hexdigest();
    }
}
