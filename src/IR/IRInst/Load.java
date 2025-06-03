package src.IR.IRInst;

import src.ASM.Operand.Reg;
import src.IR.IRVisitor;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;
import src.utils.IRType.IRType;

import java.util.HashMap;
import java.util.HashSet;

public class Load extends IRInst {

    public IRType irType;
    public Register src;

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Load(IRType irType, Register src, Register dest) {
        this.irType = irType;
        this.src = src;
        this.dest = dest;
    }

    @Override
    public void print() {
        System.out.print("  ");
        dest.print();
        System.out.print(" = load ");
        irType.print();
        System.out.print(", ptr ");
        src.print();
        System.out.print("\n");
    }

    @Override
    public HashSet<String> getUse() {
        HashSet<String> d = new HashSet<>();
        d.add(src.name);
        return d;
    }

    @Override
    public String getDef() {
        return dest.name;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        Load n = new Load(irType, src, dest);
        if (src.name.startsWith("@")) {
            n.src = src;
        } else if (!param.containsKey(src)) {
            n.src = Register.newReg(src.type, src.name + suffix);
        } else {
            if (param.get(src) instanceof Register r) {
                n.src = r;
            }
        }
        if (dest.name.startsWith("@")) {
            n.dest = dest;
        } else if (!param.containsKey(dest)) {
            n.dest = Register.newReg(dest.type, dest.name + suffix);
        } else {
            if (param.get(dest) instanceof Register r) {
                n.dest = r;
            }
        }
        return n;
    }

    @Override
    public void CP(String str, long value) {

    }
}
