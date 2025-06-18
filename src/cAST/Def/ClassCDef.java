package src.cAST.Def;

import src.cAST.BaseCASTNode;

import java.util.ArrayList;

public class ClassCDef extends CDef {
    public CConstructor ct;
    public ArrayList<VarCDef> classMem = new ArrayList<>();
    public ArrayList<FuncCDef> classFunc = new ArrayList<>();

    public String className;

    @Override
    public void addChild(BaseCASTNode c) {
        if (c instanceof FuncCDef f) {
            classFunc.add(f);
            return;
        }
        if (c instanceof VarCDef f) {
            classMem.add(f);
            return;
        }
        if (ct == null && c instanceof CConstructor f) {
            ct = f;
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}