package src.cAST.Expr;

import src.cAST.BaseCASTNode;

import java.util.ArrayList;

public class ArrayLiteralCExpr extends CExpr {
    public ArrayList<CExpr> elements = new ArrayList<>();

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr) {
            elements.add((CExpr) c);
        } else {
            throw new RuntimeException("ArrayLiteralExpr can only have Expr children");
        }
    }

}
