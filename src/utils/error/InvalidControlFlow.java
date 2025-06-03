package src.utils.error;

import src.utils.pos.Position;

public class InvalidControlFlow extends error {
    public InvalidControlFlow(Position p) {
        super("Invalid Control Flow", p);
    }
}
