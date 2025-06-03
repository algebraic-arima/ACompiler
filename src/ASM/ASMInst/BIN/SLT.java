package src.ASM.ASMInst.BIN;

public class SLT extends BinInst{
    public SLT(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  slt " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
