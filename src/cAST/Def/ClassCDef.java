package src.cAST.Def;

import src.cAST.BaseCASTNode;

import java.util.ArrayList;

public class ClassCDef extends CDef {
//    public ArrayList<VarDef> classMem;
    public ArrayList<FuncCDef> classFunc = new ArrayList<>();

    @Override
    public void addChild(BaseCASTNode c) {
        if(c instanceof FuncCDef f){
            classFunc.add(f);
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}