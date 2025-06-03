package src.ASM.ASMInst;

import src.ASM.Operand.Reg;

public class LA extends ASMInst{
    public Reg rd;
    public String fromLabel;

    public LA(String rd_, String fromLabel_) {
        rd = Reg.get(rd_);
        fromLabel = fromLabel_;
    }

    public void print() {
        System.out.println("  la " + rd.toString() + ", " + fromLabel);
    }
}
