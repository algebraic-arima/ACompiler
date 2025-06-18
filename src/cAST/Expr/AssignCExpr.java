package src.cAST.Expr;

import src.AST.Expr.AssignExpr;
import src.AST.Expr.Expr;
import src.cAST.BaseCASTNode;

import java.util.HashMap;

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


    @Override
    public HashMap<CExpr, Expr> diff(Expr e) {
        if (e.hash.equals(hash)) {
            return new HashMap<>();
        }
        HashMap<CExpr, Expr> diffMap = new HashMap<>();
        if (e instanceof AssignExpr assignExpr) {
            diffMap.putAll(var.diff(assignExpr.var));
            diffMap.putAll(value.diff(assignExpr.value));
            if(diffMap.containsKey(var) && diffMap.containsKey(value)) {
                diffMap.clear();
                diffMap.put(this, e);
            }
        } else {
            diffMap.put(this, e);
        }
        return diffMap;
    }
}
