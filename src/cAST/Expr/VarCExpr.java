package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class VarCExpr extends CExpr {
//    public String varName;

    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
