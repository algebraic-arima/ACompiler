package src.ASM.ASMInst.BR;

import src.ASM.Operand.Reg;

public class BNE extends BR {
    public Reg rs1, rs2;

    public BNE(String rs1_, String rs2_, String label_) {
        rs1 = Reg.get(rs1_);
        rs2 = Reg.get(rs2_);
        label = label_.replace("-", "_");
    }

    public void print() {
        System.out.println("  bne " + rs1.toString() + ", " + rs2.toString() + ", " + label);
    }
}
