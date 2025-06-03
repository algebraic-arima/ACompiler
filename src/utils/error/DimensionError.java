package src.utils.error;

import src.utils.pos.Position;

public class DimensionError extends error {
    public DimensionError(Position p) {
        super("Dimension Out Of Bound", p);
    }
}
