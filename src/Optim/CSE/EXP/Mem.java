package src.Optim.CSE.EXP;

import src.utils.Entity.Register;

public class Mem extends EXP {

    public Register srcAddr;

    public Mem(Register s, Register d) {
        srcAddr = s;
        dest = d;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Mem m) {
            return srcAddr.equals(m.srcAddr);
        }
        return false;
    }
}
