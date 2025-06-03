package src.IR.IRInst;

import src.IR.IRNode;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;

import java.util.HashMap;
import java.util.HashSet;

public abstract class IRInst extends IRNode {

    public Register dest;
    public HashSet<Register> liveOut = new HashSet<>();
    public int omega;
    public boolean DCER = false;

    public void addOut(String c) {
        liveOut.add(Register.registers.get(c));
    }

    abstract public void print();

    abstract public HashSet<String> getUse();

    abstract public String getDef();

    abstract public IRInst rename(String suffix, HashMap<Register, Entity> param); // for inline

    abstract public void CP(String str, long value); // for SCCP
}
