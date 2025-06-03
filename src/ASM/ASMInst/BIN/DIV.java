package src.ASM.ASMInst.BIN;

public class DIV extends BinInst{
    public DIV(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  div " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
