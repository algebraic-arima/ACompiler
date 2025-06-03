package src.ASM.ASMInst.BIN;

public class XOR extends BinInst{
    public XOR(String dest_, String lhs_, String rhs_) {
        super(lhs_, rhs_, dest_);
    }

    public void print() {
        System.out.println("  xor " + dest.toString() + ", " + lhs.toString() + ", " + rhs.toString());
    }
}
