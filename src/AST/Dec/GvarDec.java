package src.AST.Dec;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;
import src.utils.type.Type;

import java.util.ArrayList;

public class GvarDec extends Dec {

    public Type type;
    public ArrayList<String> varName;

    public GvarDec(Position p) {
        super(p);
        varName = new ArrayList<>();
    }

    @Override
    public void accept(__ASTVisitor v) {
        v.visit(this);
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visit(this);
    }
}
