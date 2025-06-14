package src.AST;

import src.AST.Dec.Dec;
import src.AST.Def.Def;
import src.utils.pos.Position;

import java.util.ArrayList;

public class Prog extends BaseASTNode {

    public ArrayList<Def> defs;
    public ArrayList<Dec> decs;
    public ArrayList<String> defHash = new ArrayList<>();

    public String hash;
    public BlakeHasher hasher = new BlakeHasher();

    public Prog(Position p) {
        super(p);
        defs = new ArrayList<>();
        decs = new ArrayList<>();
    }

    public void setHash(){
        if (hash != null) return;
        ArrayList<Object> tmp = new ArrayList<>(defs);
        hasher.unorderedMix(tmp);
        defs.forEach(d -> defHash.add(d.hash));
        hash = hasher.hexdigest();
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