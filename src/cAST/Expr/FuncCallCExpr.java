package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class FuncCallCExpr extends CExpr {
    //    public String funcName;
    public RowCExpr args;

    @Override
    public void addChild(BaseCASTNode c) {
        if (args == null && c instanceof RowCExpr e) {
            args = e;
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
