package src.cAST.Expr;

import src.cAST.BaseCASTNode;

import java.util.ArrayList;

public class RowCExpr extends CExpr {
    public ArrayList<CExpr> exps = new ArrayList<>();

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr e) {
            exps.add(e);
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
