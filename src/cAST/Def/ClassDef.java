package src.cAST.Def;

import src.cAST.BaseCASTNode;

import java.util.ArrayList;

public class ClassDef extends Def {
//    public ArrayList<VarDef> classMem;
    public ArrayList<FuncDef> classFunc = new ArrayList<>();

    @Override
    void addChild(BaseCASTNode c) {
        if(c instanceof FuncDef f){
            classFunc.add(f);
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}