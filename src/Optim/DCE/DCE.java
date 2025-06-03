package src.Optim.DCE;

import src.IR.IRDef.IRFuncDef;
import src.IR.IRProg;

public class DCE {

    public IRProg irProg;

    public DCE(IRProg irProg) {
        this.irProg = irProg;
        for (IRFuncDef func : irProg.funcDefs) {
            FuncDCE f = new FuncDCE(func);
        }
    }

}
