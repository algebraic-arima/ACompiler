package src.IR.IRInst;

import src.IR.IRVisitor;
import src.utils.Entity.*;

import java.util.HashMap;
import java.util.HashSet;

public class Move extends IRInst {

    public Entity src;
    public boolean coalesce = false;

    public Move(Entity src, Register dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public void print() {

    }

    @Override
    public void accept(IRVisitor visitor) {

    }


    @Override
    public HashSet<String> getUse() {
        return null;
    }

    @Override
    public String getDef() {
        return null;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        Move n = new Move(src, dest);
        if (src instanceof Register r) {
            n.src = Register.newReg(r.type, r.name + suffix);
        }
        n.dest = Register.newReg(dest.type, dest.name + suffix);
        return n;
    }

    @Override
    public void CP(String str, long value) {

    }
}
