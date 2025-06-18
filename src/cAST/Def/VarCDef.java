package src.cAST.Def;

import src.cAST.BaseCASTNode;
import src.cAST.Expr.CExpr;

import java.util.LinkedHashMap;

public class VarCDef extends CDef {
    public LinkedHashMap<String, CExpr> initVals;

    @Override
    public void addChild(BaseCASTNode c) {

    }
}
