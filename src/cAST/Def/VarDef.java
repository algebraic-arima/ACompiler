package src.cAST.Def;

import src.cAST.BaseCASTNode;
import src.cAST.Expr.Expr;

import java.util.LinkedHashMap;

public class VarDef extends Def {
    public LinkedHashMap<String, Expr> initVals;

    @Override
    void addChild(BaseCASTNode c) {

    }
}
