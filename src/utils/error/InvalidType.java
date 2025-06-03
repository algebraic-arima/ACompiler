package src.utils.error;

import src.utils.pos.Position;

public class InvalidType extends error {
    public InvalidType(Position p) {
        super("Invalid Type", p);
    }
}
