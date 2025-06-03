package src.AST.Stmt;

import src.AST.ASTVisitor;
import src.AST.BlakeHasher;
import src.AST.__ASTVisitor;
import src.AST.BaseASTNode;
import src.utils.pos.Position;

public abstract class Stmt extends BaseASTNode {

    public String hash;
    public BlakeHasher hasher = new BlakeHasher();

    public Stmt(Position p){
        super(p);
    }

    abstract public void setHash();

    abstract public void accept(__ASTVisitor v);

    abstract public <T> T accept(ASTVisitor<T> v);

}
