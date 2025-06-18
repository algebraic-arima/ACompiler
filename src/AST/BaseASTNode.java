package src.AST;

import src.utils.pos.Position;

import java.io.DataOutputStream;

public abstract class BaseASTNode {
    public Position pos;
    public int cacheInd = -1;
    public boolean compileFlag = true;

    public BaseASTNode(){
        pos = new Position();
    }

    public BaseASTNode(Position p) {
        pos = p;
    }

//    abstract public void writeData(DataOutputStream dos);
    public void setNoCompile(){
        compileFlag = false;
    }

    public boolean isCompile() {
        return compileFlag;
    }

    abstract public void accept(__ASTVisitor v);

    abstract public <T> T accept(ASTVisitor<T> v);
}