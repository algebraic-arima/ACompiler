package src.ASM.ASMInst.BIN;

public class ADDI extends BinInst {
    public ADDI(String dest_, String lhs_, long rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  addi " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
