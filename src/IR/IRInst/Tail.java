package src.IR.IRInst;

import src.IR.IRVisitor;
import src.utils.Entity.Constant;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;
import src.utils.IRType.IRType;
import src.utils.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Tail extends IRInst{

    public IRType retType;
    public String funcName;
    public ArrayList<Entity> args;
    public ArrayList<IRType> argTypes;
    public Ret ret;

    public Tail(String name, Type type, Register dest) {
        args = new ArrayList<>();
        argTypes = new ArrayList<>();
        funcName = name;
        retType = new IRType(type);
        this.dest = dest;
    }

    public Tail(String name, IRType type, Register dest) {
        args = new ArrayList<>();
        argTypes = new ArrayList<>();
        funcName = name;
        retType = type;
        this.dest = dest;
    }

    @Override
    public void print() {
        System.out.print("  ");
        if (dest != null && !retType.typeName.equals("void")) {
            dest.print();
            System.out.print(" = ");
        }
        System.out.print("tail call ");
        retType.print();
        System.out.print(" ");
        System.out.print(funcName);
        System.out.print("(");
        for (int i = 0; i < args.size(); i++) {
            if (argTypes.get(i) != null) {
                argTypes.get(i).print();
            } else {
                System.out.print("ptr");
            }
            System.out.print(" ");
            if (args.get(i) != null) {
                args.get(i).print();
            } else {
                System.out.print("null");
            }
            if (i != args.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println(")");
        ret.print();
    }

    @Override
    public HashSet<String> getUse() {
        HashSet<String> d = new HashSet<>();
        for (Entity e : args) {
            if (e instanceof Register r) {
                d.add(r.name);
            }
        }
        return d;
    }

    @Override
    public String getDef() {
        if (retType.typeName.equals("void")) {
            return null;
        } else if (dest != null) {
            return dest.name;
        }
        return null;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        Tail n = new Tail(funcName, retType, dest);
        for (var e : args) {
            Entity ne;
            if (e instanceof Register r) {
                if (r.name.startsWith("@")) {
                    ne = e;
                } else if (!param.containsKey(r)) {
                    ne = Register.newReg(r.type, r.name + suffix);
                } else {
                    ne = param.get(r);
                }
            } else {
                ne = e;
            }
            n.args.add(ne);
        }
        n.argTypes.addAll(argTypes);
        if (n.dest != null) {
            n.dest = Register.newReg(retType, dest.name + suffix);
        }
        return n;
    }

    @Override
    public void CP(String str, long value) {
        for (int i = 0; i < args.size(); ++i) {
            Entity l = args.get(i);
            if (l instanceof Register r && r.name.equals(str)) {
                IRType t = argTypes.get(i);
                if (t.typeName.equals("i32")) {
                    args.set(i, new Constant(value));
                } else if (t.typeName.equals("i1")) {
                    args.set(i, new Constant(value, true));
                }
            }
        }
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
