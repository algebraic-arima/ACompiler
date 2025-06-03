package src.AST.Dec;

import src.AST.BaseASTNode;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

public abstract class Dec extends BaseASTNode {

    public Dec(Position p) {
        super(p);
    }

    abstract public void accept(__ASTVisitor v);

    abstract public <T> T accept(src.AST.ASTVisitor<T> v);

}