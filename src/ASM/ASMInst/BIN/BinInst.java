package src.ASM.ASMInst.BIN;

import src.ASM.ASMInst.ASMInst;
import src.ASM.Operand.Imm;
import src.ASM.Operand.Operand;
import src.ASM.Operand.Reg;

public class BinInst extends ASMInst {
    public Operand lhs, rhs, dest;

    public BinInst(String lhs_, String rhs_, String dest_) {
        lhs = Reg.get(lhs_);
        rhs = Reg.get(rhs_);
        dest = Reg.get(dest_);
    }

    public BinInst(String lhs_, long rhs_, String dest_) {
        lhs = Reg.get(lhs_);
        rhs = new Imm(rhs_);
        dest = Reg.get(dest_);
    }

    @Override
    public void print() {
    }
}
