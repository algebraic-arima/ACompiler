package src.Optim.CSE.EXP;

import src.utils.Entity.Entity;
import src.utils.Entity.Register;

public class NonAbel extends EXP {

    String op;
    Entity lhs, rhs;

    public NonAbel(String op, Entity lhs, Entity rhs, Register d) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        dest = d;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NonAbel a) {
            return a.op.equals(op) && (lhs.equals(a.lhs) && rhs.equals(a.rhs));
        }
        return false;
    }
}
