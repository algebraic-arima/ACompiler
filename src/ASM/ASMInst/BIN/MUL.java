package src.ASM.ASMInst.BIN;

public class MUL extends BinInst {
    public MUL(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  mul " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
