package src.cAST.Expr;


import src.cAST.BaseCASTNode;

public class MemberObjAccessCExpr extends CExpr {
    public CExpr obj;
//    public String member;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr e) {
            if (obj == null) {
                obj = e;
                return;
            }
            throw new RuntimeException("AST type not match!");
        }
        throw new RuntimeException("AST type not match!");
    }
}
