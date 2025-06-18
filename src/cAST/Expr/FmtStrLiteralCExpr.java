package src.cAST.Expr;

import src.cAST.BaseCASTNode;

import java.util.ArrayList;

public class FmtStrLiteralCExpr extends CExpr {
//    public ArrayList<String> strs;
    public ArrayList<CExpr> CExprs = new ArrayList<>();

    @Override
    public void addChild(BaseCASTNode c) {
        if(c instanceof CExpr e){
            CExprs.add(e);
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
