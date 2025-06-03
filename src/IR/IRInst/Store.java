package src.IR.IRInst;

import org.antlr.v4.runtime.misc.Pair;
import src.IR.IRVisitor;
import src.utils.Entity.Constant;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;
import src.utils.IRType.IRType;

import java.util.HashMap;
import java.util.HashSet;

public class Store extends IRInst {

    public IRType irType;
    public Entity value;
    // dest: ptr to the destination memory

    public Store(IRType irType, Entity value, Register dest) {
        this.irType = irType;
        this.value = value;
        this.dest = dest;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void print() {
        System.out.print("  store ");
        irType.print();
        System.out.print(" ");
        if (value == null) {
            System.out.print("null");
        } else {
            value.print();
        }
        System.out.print(", ptr ");
        dest.print();
        System.out.print("\n");
    }

    @Override
    public HashSet<String> getUse() {
        HashSet<String> d = new HashSet<>();
        if (value instanceof Register r) {
            d.add(r.name);
        }
        d.add(dest.name);
        return d;
    }

    @Override
    public String getDef() {
        return null;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        Store n = new Store(irType, value, dest);
        if (dest.name.startsWith("@")) {
            n.dest = dest;
        } else if (!param.containsKey(dest)) {
            n.dest = Register.newReg(dest.type, dest.name + suffix);
        } else {
            if (param.get(dest) instanceof Register r) {
                n.dest = r;
            }
        }
        if (value instanceof Register r) {
            if (r.name.startsWith("@")) {
                n.value = value;
            } else if (!param.containsKey(r)) {
                n.value = Register.newReg(r.type, r.name + suffix);
            } else {
                n.value = param.get(r);
            }
        }
        return n;
    }

    @Override
    public void CP(String str, long v) {
        if (value instanceof Register r && r.name.equals(str)) {
            if (irType.typeName.equals("i32")) {
                value = new Constant(v);
            } else if (irType.typeName.equals("i1")) {
                value = new Constant(v, true);
            }
        }
    }

}
