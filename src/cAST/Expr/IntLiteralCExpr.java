package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class IntLiteralCExpr extends CExpr {
//    public Long value;

    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
