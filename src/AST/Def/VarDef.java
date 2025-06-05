package src.AST.Def;

import src.AST.ASTVisitor;
import src.AST.BlakeHasher;
import src.AST.__ASTVisitor;
import src.AST.Expr.Expr;
import src.utils.pos.Position;
import src.utils.type.Type;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class VarDef extends Def{

    public Type type;
    public LinkedHashMap<String, Expr> initVals;


    public VarDef(Position p) {
        super(p);
        initVals = new LinkedHashMap<>();
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("VarDef");
        for (Map.Entry<String, Expr> entry : initVals.entrySet()) {
            hasher.update(entry.getKey(), entry.getValue());
        }
        hash = hasher.hexdigest();
    }

    @Override
    public void printHash() {

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
