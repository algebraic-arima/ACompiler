package src.cAST.Expr;
import src.AST.Expr.BinaryLogicExpr;
import src.AST.Expr.Expr;
import src.cAST.BaseCASTNode;

import java.util.HashMap;

public class BinaryLogicCExpr extends CExpr {

    public enum BLogicOp {
        AND, OR
    }

    public CExpr lhs, rhs;
    public BLogicOp op;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof CExpr) {
            if (lhs == null) {
                lhs = (CExpr) c;
            } else if (rhs == null) {
                rhs = (CExpr) c;
            } else {
                throw new RuntimeException("BinaryLogicExpr can only have two children: lhs and rhs");
            }
        } else {
            throw new RuntimeException("BinaryLogicExpr can only have Expr children");
        }
    }

    @Override
    public HashMap<CExpr, Expr> diff(Expr e) {
        if (e.hash.equals(hash)) {
            return new HashMap<>();
        }
        HashMap<CExpr, Expr> diffMap = new HashMap<>();
        if (e instanceof BinaryLogicExpr binaryLogicExpr) {
            diffMap.putAll(lhs.diff(binaryLogicExpr.lhs));
            diffMap.putAll(rhs.diff(binaryLogicExpr.rhs));
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
