package src.cAST.Expr;

import src.AST.Expr.ArrayLiteralExpr;
import src.AST.Expr.Expr;
import src.cAST.BaseCASTNode;

import java.util.ArrayList;
import java.util.HashMap;

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

    @Override
    public HashMap<CExpr, Expr> diff(Expr e) {
        if (e.hash.equals(hash)) {
            return new HashMap<>();
        }
        HashMap<CExpr, Expr> diffMap = new HashMap<>();
        if (e instanceof ArrayLiteralExpr a && a.elements.size() == elements.size()) {
            for (int i = 0; i < elements.size(); i++) {
                diffMap.putAll(elements.get(i).diff(a.elements.get(i)));
            }
        } else {
            diffMap.put(this, e);
        }
        return diffMap;
    }

}
