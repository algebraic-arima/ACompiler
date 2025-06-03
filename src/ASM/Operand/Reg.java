package src.ASM.Operand;

import java.util.HashMap;

public class Reg extends Operand {
    public int index;
    public String name;

    public Reg(int index, String name) {
        this.index = index;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    static public Reg zero = new Reg(0, "zero");
    static public Reg ra = new Reg(1, "ra");
    static public Reg sp = new Reg(2, "sp");
    static public Reg gp = new Reg(3, "gp");
    static public Reg tp = new Reg(4, "tp");
    static public Reg t0 = new Reg(5, "t0");
    static public Reg t1 = new Reg(6, "t1");
    static public Reg t2 = new Reg(7, "t2");
    static public Reg s0 = new Reg(8, "s0");
    static public Reg s1 = new Reg(9, "s1");
    static public Reg a0 = new Reg(10, "a0");
    static public Reg a1 = new Reg(11, "a1");
    static public Reg a2 = new Reg(12, "a2");
    static public Reg a3 = new Reg(13, "a3");
    static public Reg a4 = new Reg(14, "a4");
    static public Reg a5 = new Reg(15, "a5");
    static public Reg a6 = new Reg(16, "a6");
    static public Reg a7 = new Reg(17, "a7");
    static public Reg s2 = new Reg(18, "s2");
    static public Reg s3 = new Reg(19, "s3");
    static public Reg s4 = new Reg(20, "s4");
    static public Reg s5 = new Reg(21, "s5");
    static public Reg s6 = new Reg(22, "s6");
    static public Reg s7 = new Reg(23, "s7");
    static public Reg s8 = new Reg(24, "s8");
    static public Reg s9 = new Reg(25, "s9");
    static public Reg s10 = new Reg(26, "s10");
    static public Reg s11 = new Reg(27, "s11");
    static public Reg t3 = new Reg(28, "t3");
    static public Reg t4 = new Reg(29, "t4");
    static public Reg t5 = new Reg(30, "t5");
    static public Reg t6 = new Reg(31, "t6");

    static public Reg[] regs = {
            zero, ra, sp, gp, tp, t0, t1, t2,
            s0, s1, a0, a1, a2, a3, a4, a5,
            a6, a7, s2, s3, s4, s5, s6, s7,
            s8, s9, s10, s11, t3, t4, t5, t6
    };

    static public String[] strs = {
            "zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2",
            "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5",
            "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7",
            "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"
    };

    static public String[] freeRegs = {
            "zer", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
            "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"
    };

    static public HashMap<String, Reg> str2Regs;

    static {
        {
            str2Regs = new HashMap<>();
            for (int i = 0; i < 32; ++i) {
                str2Regs.put(strs[i], regs[i]);
            }
        }
    }

    static public Reg get(String str) {
        return str2Regs.get(str);
    }

    public Reg(int ind) {
        this.index = ind;
        this.name = regs[ind].name;
    }

}
