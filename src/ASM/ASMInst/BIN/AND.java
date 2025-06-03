package src.ASM.ASMInst.BIN;

public class AND extends BinInst{
    public AND(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  and "+ dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
