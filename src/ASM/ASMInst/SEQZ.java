package src.ASM.ASMInst;

import src.ASM.Operand.Reg;

public class SEQZ extends ASMInst{

    public Reg rd, rs;

    public SEQZ(String rd_, String rs_) {
        rd = Reg.get(rd_);
        rs = Reg.get(rs_);
    }

    public void print() {
        System.out.println("  seqz " + rd.toString() + ", " + rs.toString());
    }
}
