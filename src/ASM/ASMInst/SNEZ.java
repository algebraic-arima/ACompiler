package src.ASM.ASMInst;

import src.ASM.Operand.Reg;

public class SNEZ extends ASMInst {
    Reg rd, rs;

    public SNEZ(String rd_, String rs_) {
        rd = Reg.get(rd_);
        rs = Reg.get(rs_);
    }

    public void print() {
        System.out.println("  snez " + rd.toString() + ", " + rs.toString());
    }
}
