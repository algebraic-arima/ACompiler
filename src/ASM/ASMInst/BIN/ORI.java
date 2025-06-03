package src.ASM.ASMInst.BIN;

public class ORI extends BinInst {
    public ORI(String dest_, String lhs_, long rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  ori " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
