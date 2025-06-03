package src.AST;

import src.AST.Dec.Dec;
import src.AST.Def.Def;
import src.utils.pos.Position;

import java.util.ArrayList;
import java.util.HashMap;

public class Prog extends BaseASTNode {

    public ArrayList<Def> defs;
    public ArrayList<Dec> decs;
    public ArrayList<String> hashs = new ArrayList<>();

    public Prog(Position p) {
        super(p);
        defs = new ArrayList<>();
        decs = new ArrayList<>();
    }

    public void setHash(){
        defs.forEach(d -> {
            d.setHash();
            hashs.add(d.hash);
        });
        printHash();
    }

    public void printHash(){
        defs.forEach(Def::printHash);
    }

    public void accept(__ASTVisitor v) {
        v.visit(this);
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visit(this);
    }
}