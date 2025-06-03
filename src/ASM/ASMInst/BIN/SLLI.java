package src.ASM.ASMInst.BIN;

public class SLLI extends BinInst {
    public SLLI(String dest_, String lhs_, long rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  slli " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
