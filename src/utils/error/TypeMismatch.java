package src.utils.error;

import src.utils.pos.Position;

public class TypeMismatch extends error {
    public TypeMismatch(Position p) {
        super("Type Mismatch", p);
    }
}
