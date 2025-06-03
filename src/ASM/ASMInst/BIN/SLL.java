package src.ASM.ASMInst.BIN;

public class SLL extends BinInst{
    public SLL(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  sll " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
