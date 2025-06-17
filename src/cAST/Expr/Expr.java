package src.cAST.Expr;

import src.cAST.BaseCASTNode;

abstract public class Expr extends BaseCASTNode{
    public String hash;
    abstract void addChild(BaseCASTNode c);
}
