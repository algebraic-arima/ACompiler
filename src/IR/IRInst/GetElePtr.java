package src.IR.IRInst;

import src.IR.IRVisitor;
import src.utils.Entity.Constant;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;
import src.utils.IRType.IRType;

import java.util.HashMap;
import java.util.HashSet;

public class GetElePtr extends IRInst {

    public IRType ptrType, destType;
    public Register ptr; // ptr = null iff ptr not initialized
    public Entity offset;
    public int fieldInd;

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public GetElePtr(String ptrType, String destType, Register ptr, Register dest, int offset, int fieldInd) {
        if (ptrType.equals("bool")) {
            this.ptrType = new IRType("int");
        } else {
            this.ptrType = new IRType(ptrType);
        }
        this.ptr = ptr;
        this.offset = new Constant(offset);
        this.fieldInd = fieldInd;
        this.destType = new IRType(destType);
        this.dest = dest;
    }

    public GetElePtr(String ptrType, String destType, Register ptr, Register dest, Entity offset, int fieldInd) {
        if (ptrType.equals("bool")) {
            this.ptrType = new IRType("int");
        } else {
            this.ptrType = new IRType(ptrType);
        }
        this.ptr = ptr;
        this.offset = offset;
        this.fieldInd = fieldInd;
        this.destType = new IRType(destType);
        this.dest = dest;
    }

    public GetElePtr(IRType ptrType, IRType destType, Register ptr, Register dest, Entity offset, int fieldInd) {
        this.ptrType = ptrType;
        this.ptr = ptr;
        this.offset = offset;
        this.fieldInd = fieldInd;
        this.destType = destType;
        this.dest = dest;
    }


    @Override
    public void print() {
        System.out.print("  ");
        dest.print();
        System.out.print(" = getelementptr ");
        ptrType.print();
        System.out.print(", ptr ");
        if (ptr != null) {
            ptr.print();
        } else {
            System.out.print("null");
        }
        System.out.print(", i32 ");
        offset.print();
        if (fieldInd != -1) {
            System.out.print(", i32 ");
            System.out.print(fieldInd);
        }
        System.out.print("\n");
    }

    @Override
    public HashSet<String> getUse() {
        HashSet<String> d = new HashSet<>();
        if (offset instanceof Register r) {
            d.add(r.name);
        }
        if (ptr != null) {
            d.add(ptr.name);
        }
        return d;
    }

    @Override
    public String getDef() {
        return dest.name;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        GetElePtr n = new GetElePtr(ptrType, destType, ptr, dest, offset, fieldInd);
        if (offset instanceof Register r) {
            if (!param.containsKey(r)) {
                n.offset = Register.newReg(offset.type, r.name + suffix);
            } else if (r.name.startsWith("@")) {
                n.offset = offset;
            } else {
                n.offset = param.get(r);
            }
        }
        if (ptr != null) {
            if (!param.containsKey(ptr)) {
                n.ptr = Register.newReg(ptr.type, ptr.name + suffix);
            } else if (ptr.name.startsWith("@")) {
                n.ptr = ptr;
            } else {
                if (param.get(ptr) instanceof Register r) {
                    n.ptr = r;
                }
            }
        }
        if (!param.containsKey(dest)) {
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
        if (offset instanceof Register r && r.name.equals(str)) {
            offset = new Constant(value);
        }
        if (ptr.name.equals(str)) {
            ptr = null;
        }
    }
}
