package src.ASM.ASMInst.BIN;

public class SRAI extends BinInst {
    public SRAI(String dest_, String lhs_, long rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  srai " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
