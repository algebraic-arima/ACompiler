package src.Optim.Mem2Reg;

import src.IR.IRDef.IRFuncDef;
import src.IR.IRProg;

import java.util.ArrayList;

public class Mem2Reg {

    public ArrayList<FuncMem2Reg> FuncMem2RegSet;

    public Mem2Reg(IRProg node) {
        FuncMem2RegSet = new ArrayList<>();
        for (IRFuncDef func : node.funcDefs) {
            FuncMem2Reg root = new FuncMem2Reg(func);
            FuncMem2RegSet.add(root);
        }
    }
}
