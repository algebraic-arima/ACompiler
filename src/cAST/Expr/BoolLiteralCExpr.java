package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class BoolLiteralCExpr extends CExpr {
    public boolean value;

    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
