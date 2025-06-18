package src.cAST.Expr;

import src.AST.Expr.BinaryArithExpr;
import src.cAST.BaseCASTNode;
import src.AST.Expr.Expr;

import java.util.HashMap;

public class BinaryArithCExpr extends CExpr {

    public enum BArithOp {
        ADD, SUB, MUL, DIV, MOD, BLS, BRS, BAND, BOR, BXOR, EQ, NE, LT, GT, LE, GE,
    }// 16 = 10 + 6

    public CExpr lhs, rhs;
    public BArithOp op;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr) {
            if (lhs == null) {
                lhs = (CExpr) c;
            } else if (rhs == null) {
                rhs = (CExpr) c;
            } else {
                throw new RuntimeException("BinaryArithExpr can only have two children: lhs and rhs");
            }
        } else {
            throw new RuntimeException("BinaryArithExpr can only have Expr children");
        }
    }

    @Override
    public HashMap<CExpr, Expr> diff(Expr e) {
        if (e.hash.equals(hash)) {
            return new HashMap<>();
        }
        HashMap<CExpr, Expr> diffMap = new HashMap<>();
        if (e instanceof BinaryArithExpr binaryArithExpr) {
            diffMap.putAll(lhs.diff(binaryArithExpr.lhs));
            diffMap.putAll(rhs.diff(binaryArithExpr.rhs));
            if(diffMap.containsKey(lhs) && diffMap.containsKey(rhs)) {
                diffMap.clear();
                diffMap.put(this, e);
            }
        } else {
            diffMap.put(this, e);
        }
        return diffMap;
    }
}
