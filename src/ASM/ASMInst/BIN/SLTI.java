package src.ASM.ASMInst.BIN;

public class SLTI extends BinInst {
    public SLTI(String dest_, String lhs_, long rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  slti " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
