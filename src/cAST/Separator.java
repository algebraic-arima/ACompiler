package src.cAST;

public class Separator extends  BaseCASTNode{
    @Override
    void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
