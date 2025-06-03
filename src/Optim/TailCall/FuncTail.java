package src.Optim.TailCall;

import src.IR.IRDef.IRBlock;
import src.IR.IRDef.IRFuncDef;
import src.IR.IRInst.*;

public class FuncTail {

    public FuncTail(IRFuncDef func_) {
        for (IRBlock b : func_.blocks) {
            int i = b.IRInsts.size() - 2;
            if (i < 0) continue;
            IRInst inst = b.IRInsts.get(i);
            IRInst n = b.IRInsts.get(i + 1);
            if (inst instanceof Call c && n instanceof Ret r) {
                if (c.dest == null && r.value == null && func_.retType.typeName.equals("void")) {
                    Tail t = new Tail(c.funcName, c.retType, c.dest);
                    t.args = c.args;
                    t.argTypes = c.argTypes;
                    t.ret = r;
                    b.IRInsts.set(i, t);
                    b.IRInsts.removeLast();
                } else if (r.value != null && c.dest == r.value) {
                    Tail t = new Tail(c.funcName, c.retType, c.dest);
                    t.args = c.args;
                    t.argTypes = c.argTypes;
                    t.ret = r;
                    b.IRInsts.set(i, t);
                    b.IRInsts.removeLast();
                }
            }
        }
    }
}
