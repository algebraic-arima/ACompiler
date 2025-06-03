package src.ASM.ASMInst;

import src.ASM.Operand.Reg;

public class LWL extends ASMInst {
    public Reg rd;
    public String fromLabel;

    public LWL(String d, String s) {
        rd = Reg.get(d);
        fromLabel = s;
    }

    public void print() {
        System.out.println("  lw " + rd.toString() + ", " + fromLabel);
    }
}
