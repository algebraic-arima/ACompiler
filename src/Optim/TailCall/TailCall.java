package src.Optim.TailCall;

import src.IR.IRProg;

import java.util.HashMap;

public class TailCall {

    HashMap<String, FuncTail> list;

    public TailCall(IRProg irProg) {
        list = new HashMap<>();
        for (var func : irProg.funcDefs) {
            FuncTail f = new FuncTail(func);
            list.put(func.name, f);
        }
    }

}
