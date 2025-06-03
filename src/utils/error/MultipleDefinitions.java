package src.utils.error;

import src.utils.pos.Position;

public class MultipleDefinitions extends error {
    public MultipleDefinitions(Position p) {
        super("Multiple Definitions", p);
    }
}
