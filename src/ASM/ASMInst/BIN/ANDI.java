package src.ASM.ASMInst.BIN;

public class ANDI extends BinInst {
    public ANDI(String dest_, String lhs_, long rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  andi " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
