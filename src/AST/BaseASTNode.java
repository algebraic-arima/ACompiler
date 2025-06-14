package src.AST;

import src.utils.pos.Position;

import java.io.DataOutputStream;

public abstract class BaseASTNode {
    public Position pos;

    public BaseASTNode(){
        pos = new Position();
    }

    public BaseASTNode(Position p) {
        pos = p;
    }

//    abstract public void writeData(DataOutputStream dos);

    abstract public void accept(__ASTVisitor v);

    abstract public <T> T accept(ASTVisitor<T> v);
}