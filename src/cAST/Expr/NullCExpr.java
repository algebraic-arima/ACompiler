package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class NullCExpr extends CExpr {
    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
