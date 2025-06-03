package src.ASM.ASMInst.BIN;

public class ADD extends BinInst {
    public ADD(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  add " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }

}
