package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class NewArrayInitCExpr extends CExpr {
    public ArrayLiteralCExpr init;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof ArrayLiteralCExpr e) {
            if (init == null) {
                init = e;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        throw new RuntimeException("AST type not match!");
    }
}
