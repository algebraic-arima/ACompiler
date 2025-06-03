package src.ASM.ASMInst.BR;

public class J extends BR {

    public J(String toLabel_) {
        label = toLabel_.replace("-", "_");
    }

    public void print() {
        System.out.println("  j " + label);
    }
}
