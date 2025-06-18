package src.cAST.Expr;

import src.cAST.BaseCASTNode;

public class AssignCExpr extends CExpr {
    public CExpr var;
    public CExpr value;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr) {
            if (var == null) {
                var = (CExpr) c;
            } else if (value == null) {
                value = (CExpr) c;
            } else {
                throw new RuntimeException("AssignExpr can only have two children: variable and value");
            }
        } else {
            throw new RuntimeException("AssignExpr can only have Expr children");
        }
    }
}
