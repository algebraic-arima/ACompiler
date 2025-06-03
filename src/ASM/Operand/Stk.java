package src.ASM.Operand;

import java.util.HashMap;

public class Stk extends Operand {
    public long pos;

    public Stk(long pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return pos + "(sp)";
    }

    static public HashMap<Long, Stk> stackRegs = new HashMap<>();

    static public Stk get(long pos) {
        if (stackRegs.containsKey(pos)) {
            return stackRegs.get(pos);
        }
        Stk s = new Stk(pos);
        stackRegs.put(pos, s);
        return s;
    }
}
