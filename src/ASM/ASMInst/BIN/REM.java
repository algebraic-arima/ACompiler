package src.ASM.ASMInst.BIN;

public class REM extends BinInst {
    public REM(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  rem "+ dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
