package src.IR.IRInst;

import src.IR.IRDef.IRBlock;
import src.IR.IRVisitor;
import src.utils.Entity.Constant;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;
import src.utils.IRType.IRType;

import java.util.HashMap;
import java.util.HashSet;

public class Ret extends terminalIRInst {

    public IRType type;
    public Entity value;

    public Ret(IRType type, Entity value) {
        this.type = type;
        this.value = value;
    }

    public Ret() {
        this.type = new IRType();
        this.value = null;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void print() {
        if (type.typeName == null) {
            System.out.print("  ret ptr null\n");
            // in irtype, typename=null: null
            // in other cases, typename can be i32,i1,ptr and void
        } else if (type.typeName.equals("void")) {
            System.out.print("  ret void\n");
        } else {
            System.out.print("  ret ");
            type.print();
            System.out.print(" ");
            value.print();
            System.out.print("\n");
        }
    }

    @Override
    public HashSet<String> getUse() {
        HashSet<String> d = new HashSet<>();
        if (value instanceof Register r) {
            d.add(r.name);
        }
        return d;
    }

    @Override
    public String getDef() {
        return null;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        return new Jmp(null);
    }

    @Override
    public void CP(String str, long v) {
        if (value instanceof Register r && r.name.equals(str)) {
            if (type.typeName.equals("i32")) {
                value = new Constant(v);
            } else if (type.typeName.equals("i1")) {
                value = new Constant(v, true);
            }
        }
    }

    public IRInst rename(IRBlock d) {
        return new Jmp(d);
    }
}
