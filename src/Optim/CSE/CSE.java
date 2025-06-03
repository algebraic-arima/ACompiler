package src.Optim.CSE;

import src.IR.IRDef.IRFuncDef;
import src.IR.IRProg;

import java.util.HashSet;

public class CSE {

    public HashSet<FuncCSE> list = new HashSet<>();

    public CSE(IRProg irProg) {
        for (IRFuncDef f : irProg.funcDefs) {
            FuncCSE c = new FuncCSE(f);
            list.add(c);
        }
    }
}
