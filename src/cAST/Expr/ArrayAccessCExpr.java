package src.cAST.Expr;

import src.cAST.BaseCASTNode;

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
}
