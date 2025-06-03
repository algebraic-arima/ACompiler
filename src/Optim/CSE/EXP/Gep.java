package src.Optim.CSE.EXP;

import src.utils.Entity.Entity;
import src.utils.Entity.Register;

public class Gep extends EXP {

    public Register ptr; // ptr = null iff ptr not initialized
    public Entity offset;
    public int fieldInd;

    public Gep(Register ptr, Entity offset, int fieldInd,Register d) {
        this.ptr = ptr;
        this.offset = offset;
        this.fieldInd = fieldInd;
        dest = d;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Gep g) {
            return g.ptr.equals(ptr) && g.offset.equals(offset) && g.fieldInd == fieldInd;
        }
        return false;
    }
}
