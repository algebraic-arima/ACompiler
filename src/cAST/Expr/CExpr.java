package src.cAST.Expr;

import src.AST.Expr.Expr;
import src.cAST.BaseCASTNode;

import java.util.HashMap;

abstract public class CExpr extends BaseCASTNode {
    public abstract void addChild(BaseCASTNode c);
    public HashMap<CExpr, Expr> diff(Expr e){
        return new HashMap<>();
    }
    // the subtree led by these nodes are different from cache
}
