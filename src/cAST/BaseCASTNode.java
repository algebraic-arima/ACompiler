package src.cAST;

abstract public class BaseCASTNode {
    public String hash;
    public String type;
    public abstract void addChild(BaseCASTNode c);
}
