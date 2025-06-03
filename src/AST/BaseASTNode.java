package src.AST;

import src.utils.pos.Position;

public abstract class BaseASTNode {
    public Position pos;

    public BaseASTNode(){
        pos = new Position();
    }

    public BaseASTNode(Position p) {
        pos = p;
    }

    abstract public void accept(__ASTVisitor v);

    abstract public <T> T accept(ASTVisitor<T> v);
}