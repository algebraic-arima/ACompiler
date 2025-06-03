package src.utils.error;

import src.utils.pos.Position;

public class MissingReturnStmt extends error {
    public MissingReturnStmt(Position p) {
        super("Missing Return Statement", p);
    }
}
