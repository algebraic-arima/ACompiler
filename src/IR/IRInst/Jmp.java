package src.IR.IRInst;

import src.IR.IRDef.IRBlock;
import src.IR.IRVisitor;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;

import java.util.HashMap;
import java.util.HashSet;

public class Jmp extends terminalIRInst {
    // unconditional branch

    public IRBlock block;

    public Jmp(IRBlock block) {
        this.block = block;
    }

    @Override
    public void print() {
        System.out.print("  br label %" + block.label + "\n");
    }

    @Override
    public HashSet<String> getUse() {
        return new HashSet<>();
    }

    @Override
    public String getDef() {
        return null;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        return new Jmp(block);
    }

    @Override
    public void CP(String str, long value) {

    }

    public IRInst rename(IRBlock d) {
        return new Jmp(d);
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
