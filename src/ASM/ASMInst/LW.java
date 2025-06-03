package src.ASM.ASMInst;

import src.ASM.Operand.Reg;

public class LW extends ASMInst {
    public Reg rd, rs1;
    public long offset;

    public LW(String d, long offset_, String s) {
        rd = Reg.get(d);
        offset = offset_;
        rs1 = Reg.get(s);
    }

    public void print() {
        System.out.println("  lw " + rd.toString() + ", " + offset + "(" + rs1.toString() + ")");
    }
}
