package src.IR;


import src.IR.IRDec.IRDec;
import src.IR.IRDef.*;

import java.util.ArrayList;

public class IRProg {
    public ArrayList<IRClassDef> classDefs = new ArrayList<>();
    public ArrayList<IRGlobalVarDef> gVarDefs = new ArrayList<>();
    public ArrayList<IRFuncDef> funcDefs = new ArrayList<>();
    public ArrayList<IRDec> decs = new ArrayList<>();
    public IRStrDef strDef = new IRStrDef();

    public void addClassDef(IRClassDef classDef) {
        classDefs.add(classDef);
    }

    public void addGVarDef(IRGlobalVarDef gVarDef) {
        gVarDefs.add(gVarDef);
    }

    public void addFuncDef(IRFuncDef funcDef) {
        funcDefs.add(funcDef);
    }

    public void addDec(IRDec dec){
        decs.add(dec);
    }

    public void print() {
        classDefs.forEach(IRClassDef::print);
        strDef.print();
        gVarDefs.forEach(IRGlobalVarDef::print);
        decs.forEach(IRDec::print);
        funcDefs.forEach(IRFuncDef::print);
    }

    public void reformat(){
        funcDefs.forEach(IRFuncDef::reformat);
    }

    public void addAlloca(){
        funcDefs.forEach(IRFuncDef::addAlloca);
    }
}
