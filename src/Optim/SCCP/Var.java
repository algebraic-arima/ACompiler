package src.Optim.SCCP;

import src.IR.IRDef.IRBlock;
import src.IR.IRInst.IRInst;
import src.utils.IRType.IRType;

import java.util.HashMap;
import java.util.HashSet;

public class Var extends SCCPNode {
    static int M = 2;
    static int C = 1;
    static int U = 0;

    public String name;
    public IRType type;
    public int p = U;
    public long value;
    public HashMap<IRInst,IRBlock> use = new HashMap<>();

    public Var(String s, IRType t) {
        name = s;
        type = t;
    }

    public Var(long val) {
        value = val;
        p = C;
    }

}
