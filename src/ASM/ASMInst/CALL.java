package src.ASM.ASMInst;

public class CALL extends ASMInst {
    public String func;

    public CALL(String func_) {
        func = func_;
    }

    public void print() {
        System.out.println("  call " + func);
    }
}
