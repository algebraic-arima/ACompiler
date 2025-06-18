package src.cAST.Expr;

import src.AST.Expr.BoolLiteralExpr;
import src.AST.Expr.Expr;
import src.cAST.BaseCASTNode;

import java.util.HashMap;

public class BoolLiteralCExpr extends CExpr {
    public boolean value;

    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }


    @Override
    public HashMap<CExpr, Expr> diff(Expr e) {
        if (e.hash.equals(hash)) {
            return new HashMap<>();
        }
        HashMap<CExpr, Expr> diffMap = new HashMap<>();
        if (e instanceof BoolLiteralExpr boolLiteral && boolLiteral.value == value) {
            return diffMap;
        } else {
            diffMap.put(this, e);
        }
        return diffMap;
    }
}
