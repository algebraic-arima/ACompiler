package src.cAST.Stmt;

import src.cAST.BaseCASTNode;
import src.cAST.Expr.BinaryLogicExpr;
import src.cAST.Expr.Expr;

public class ExprStmt extends Stmt {
    public Expr expr;

    @Override
    void addChild(BaseCASTNode c) {
        if(expr == null && c instanceof Expr e){
            expr = e;
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
