package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class StringLiteralCExpr extends CExpr {
//    public String value;
    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
