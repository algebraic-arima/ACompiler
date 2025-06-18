package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class MemberFuncCallCExpr extends CExpr {
    public CExpr obj;
//    public String funcName;
    public RowCExpr args;


    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof RowCExpr e) {
            if (args == null) {
                args = e;
                return;
            }
            throw new RuntimeException("AST type not match!");
        } else if (c instanceof CExpr e) {
            if (obj == null) {
                obj = e;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        throw new RuntimeException("AST type not match!");
    }
}
