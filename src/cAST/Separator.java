package src.cAST;

public class Separator extends BaseCASTNode{
    static String h = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    public Separator() {
        this.hash = h;
    }
    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
