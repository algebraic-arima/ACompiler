package src.cAST.Expr;

import src.AST.Expr.ArrayAccessExpr;
import src.AST.Expr.Expr;
import src.cAST.BaseCASTNode;

import java.util.HashMap;

public class ArrayAccessCExpr extends CExpr {
    public CExpr array;
    public CExpr index;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr) {
            if (array == null) {
                array = (CExpr) c;
            } else if (index == null) {
                index = (CExpr) c;
            } else {
                throw new RuntimeException("ArrayAccessExpr can only have two children: array and index");
            }
        } else {
            throw new RuntimeException("ArrayAccessExpr can only have Expr children");
        }
    }

    @Override
    public HashMap<CExpr, Expr> diff(Expr e) {
        if (e.hash.equals(hash)) {
            return new HashMap<>();
        }
        HashMap<CExpr, Expr> diffMap = new HashMap<>();
        if (e instanceof ArrayAccessExpr arrayAccessExpr) {
            diffMap.putAll(array.diff(arrayAccessExpr.array));
            diffMap.putAll(index.diff(arrayAccessExpr.index));
        } else {
            diffMap.put(this, e);
        }
        return diffMap;
    }
}
