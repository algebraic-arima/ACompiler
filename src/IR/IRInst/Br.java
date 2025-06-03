package src.IR.IRInst;

import src.ASM.Operand.Reg;
import src.IR.IRDef.IRBlock;
import src.IR.IRVisitor;
import src.utils.Entity.Constant;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;

import java.util.HashMap;
import java.util.HashSet;

public class Br extends terminalIRInst {

    public Entity cond;
    public Icmp cmp = null;
    public IRBlock trueBlock, falseBlock;

    public Br(Entity cond, IRBlock trueBlock, IRBlock falseBlock) {
        this.cond = cond;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    public Br(Entity cond) {
        this.cond = cond;
        this.trueBlock = null;
        this.falseBlock = null;
    }

    @Override
    public void print() {
        if (cmp != null) cmp.print();
        System.out.print("  br i1 ");
        cond.print();
        System.out.print(", label %");
        System.out.print(trueBlock.label);
        System.out.print(", label %");
        System.out.print(falseBlock.label);
        System.out.print("\n");
    }

    @Override
    public HashSet<String> getUse() {
        if (cmp == null) {
            HashSet<String> d = new HashSet<>();
            if (cond instanceof Register r) {
                d.add(r.name);
            }
            return d;
        } else {
            return cmp.getUse();
        }
    }

    @Override
    public String getDef() {
        return null;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        Br n = new Br(cond, trueBlock, falseBlock);
        if (cond instanceof Register r) {
            if (!param.containsKey(r)) {
                n.cond = Register.newReg(r.type, r.name + suffix);
            } else {
                n.cond = param.get(r);
            }
        }
        return n;
    }

    @Override
    public void CP(String str, long value) {
        if (cond instanceof Register r && r.name.equals(str)) {
            cond = new Constant(value == 0 ? 0 : 1, true);
        }
    }

    public IRInst rename(String suffix, HashMap<Register, Entity> param, IRBlock t, IRBlock f) {
        Br n = new Br(cond, t, f);
        if (cond instanceof Register r) {
            if (!param.containsKey(r)) {
                n.cond = Register.newReg(r.type, r.name + suffix);
            } else {
                n.cond = param.get(r);
            }
        }
        return n;
    }


    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
