package src.ASM.ASMInst;

import src.ASM.Operand.Reg;

public class LI extends ASMInst{
    public Reg rd;
    public long imm;

    public LI(String rd_, long imm_) {
        rd = Reg.get(rd_);
        imm = imm_;
    }

    public void print() {
        System.out.println("  li " + rd.toString() + ", " + imm);
    }
}
