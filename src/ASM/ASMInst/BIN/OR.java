package src.ASM.ASMInst.BIN;

public class OR extends BinInst {
    public OR(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  or " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
