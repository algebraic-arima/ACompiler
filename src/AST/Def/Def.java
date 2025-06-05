package src.AST.Def;

import src.AST.BlakeHasher;
import src.AST.__ASTVisitor;
import src.AST.BaseASTNode;
import src.utils.pos.Position;

public abstract class Def extends BaseASTNode {
    public String hash;
    public BlakeHasher hasher;

    public Def(Position p) {
        super(p);
        hasher = new BlakeHasher();
    }

    abstract public void setHash();

    abstract public void printHash();

    abstract public void accept(__ASTVisitor v);

    abstract public <T> T accept(src.AST.ASTVisitor<T> v);

}