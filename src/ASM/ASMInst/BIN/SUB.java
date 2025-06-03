package src.ASM.ASMInst.BIN;

public class SUB extends BinInst{
    public SUB(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  sub " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
