package src.IR.IRInst;

import org.antlr.v4.runtime.misc.Pair;
import src.IR.IRDef.IRBlock;
import src.IR.IRVisitor;
import src.utils.Entity.*;
import src.utils.IRType.IRType;
import src.utils.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Phi extends IRInst {

    public IRType irType;
    public ArrayList<Pair<Entity, IRBlock>> valList;

    public Phi(Register dest, Type type) {
        this.dest = dest;
        this.irType = new IRType(type);
        valList = new ArrayList<>();
    }

    public Phi(Register dest, IRType type) {
        this.dest = dest;
        this.irType = type;
        valList = new ArrayList<>();
    }

    public void addList(Entity val, IRBlock label) {
        valList.add(new Pair<>(val, label));
    }

    @Override
    public void accept(IRVisitor visitor) {
    }

    @Override
    public void print() {
        System.out.print("  ");
        dest.print();
        System.out.print(" = phi ");
        irType.print();
        for (int i = 0; i < valList.size(); ++i) {
            System.out.print(" [");
            valList.get(i).a.print();
            System.out.print(", ");
            System.out.print("%" + valList.get(i).b.label);
            System.out.print("]");
            if (i != valList.size() - 1) {
                System.out.print(",");
            }
        }
        System.out.print("\n");
    }

    @Override
    public HashSet<String> getUse() {
        HashSet<String> d = new HashSet<>();
        for (var e : valList) {
            if (e.a instanceof Register r) {
                d.add(r.name);
            }
        }
        return d;
    }

    @Override
    public String getDef() {
        return dest.name;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        Phi n = new Phi(dest, irType);
        for (var e : valList) {
            Pair<Entity, IRBlock> ne;
            if (e.a instanceof Register r) {
                if (r.name.startsWith("@")) {
                    ne = new Pair<>(r, e.b);
                } else if (!param.containsKey(r)) {
                    Register newReg = Register.newReg(r.type, r.name + suffix);
                    ne = new Pair<>(newReg, e.b);
                } else {
                    ne = new Pair<>(param.get(r), e.b);
                }
            } else {
                ne = new Pair<>(e.a, e.b);
            }
            n.valList.add(ne);
        }
        n.dest = Register.newReg(irType, dest.name + suffix);
        return n;
    }

    @Override
    public void CP(String str, long value) {
        for (int i = 0; i < valList.size(); ++i) {
            var v = valList.get(i);
            Entity l = v.a;
            if (l instanceof Register r && r.name.equals(str)) {
                if (irType.typeName.equals("i32")) {
                    valList.set(i, new Pair<>(new Constant(value), v.b));
                } else if (irType.typeName.equals("i1")) {
                    valList.set(i, new Pair<>(new Constant(value, true), v.b));
                }
            }
        }
    }
}
