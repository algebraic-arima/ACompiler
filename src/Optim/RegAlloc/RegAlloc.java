package src.Optim.RegAlloc;


import src.IR.IRDef.IRFuncDef;
import src.IR.IRProg;

import java.util.HashMap;

public class RegAlloc {

    public HashMap<String, FuncRegAlloc> r;

    public RegAlloc(IRProg prog) {
        r = new HashMap<>();
        for (IRFuncDef func : prog.funcDefs) {
            FuncRegAlloc l = new FuncRegAlloc(func);
            r.put(func.name, l);
            func.regNum = l.rig.colors.size();
        }
    }

}
