package src.utils.error;

import src.utils.pos.Position;

public class FunctionMainError extends error{
    public FunctionMainError(Position p){
        super("Main Function Error", p);
    }
}
