package src.cAST.Expr;

import src.cAST.BaseCASTNode;

import java.util.ArrayList;

public class NewArrayCExpr extends CExpr {
    public ArrayList<CExpr> len = new ArrayList<>();
//    public int dim;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr e) {
            len.add(e);
            return;
        }
        throw new RuntimeException("AST type not match!");
    }

}
