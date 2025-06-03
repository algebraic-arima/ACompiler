package src.ASM.ASMInst;

import src.ASM.Operand.Operand;
import src.ASM.Operand.Reg;

public class MV extends ASMInst {
    public Operand dest;
    public Operand src;

    public MV() {
        dest = null;
        src = null;
    }

    public MV(String dest_, String src_) {
        dest = Reg.get(dest_);
        src = Reg.get(src_);
    }

    public void print() {
        System.out.println("  mv " + dest.toString() + ", " + src.toString());
    }
}
