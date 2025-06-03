package src.ASM.ASMInst;

import src.ASM.Operand.Reg;

public class SW extends ASMInst {
    public Reg val, destAddr;
    public long offset;

    public SW(String src_, long offset_, String destAddr_) {
        val = Reg.get(src_);
        offset = offset_;
        destAddr = Reg.get(destAddr_);
    }

    public void print() {
        System.out.println("  sw " + val.toString() + ", " + offset + "(" + destAddr.toString() + ")");
    }
}
