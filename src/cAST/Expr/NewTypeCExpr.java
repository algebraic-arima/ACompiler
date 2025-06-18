package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class NewTypeCExpr extends CExpr {
    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
