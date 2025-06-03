package src.utils.error;

import src.utils.pos.Position;

public class InvalidIdentifier extends error{
    InvalidIdentifier(Position p){
        super("Invalid Identifier",p);
    }
}
