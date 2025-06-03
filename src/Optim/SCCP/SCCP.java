package src.Optim.SCCP;

import src.IR.IRProg;

import java.util.HashMap;

public class SCCP {

    public HashMap<String, FuncSCCP> list;

    public SCCP(IRProg irProgram_) {
        list = new HashMap<>();
        for (var func : irProgram_.funcDefs) {
            FuncSCCP f = new FuncSCCP(func);
            list.put(func.name, f);
        }
    }
}
