package src.utils.error;

import src.utils.pos.Position;

public class SyntaxError extends error {

    public SyntaxError(String msg, Position pos) {
        super("Syntax Error", pos);
    }
}
