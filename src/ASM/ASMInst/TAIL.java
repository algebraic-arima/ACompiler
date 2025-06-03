package src.ASM.ASMInst;

public class TAIL extends ASMInst{
    public String func;

    public TAIL(String func_) {
        func = func_;
    }

    public void print() {
        System.out.println("  tail " + func);
    }
}
