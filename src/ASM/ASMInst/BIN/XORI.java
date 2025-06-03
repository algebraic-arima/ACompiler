package src.ASM.ASMInst.BIN;

public class XORI extends BinInst {
    public XORI(String dest_, String lhs_, long rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  xori " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
