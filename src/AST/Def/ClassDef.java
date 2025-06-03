package src.AST.Def;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

import java.util.ArrayList;

public class ClassDef extends Def {
    public String className;
    public Constructor constructor;
    public ArrayList<VarDef> classMem;
    public ArrayList<FuncDef> classFunc;

    public ClassDef(Position p) {
        super(p);
        classMem = new ArrayList<>();
        classFunc = new ArrayList<>();
    }

    @Override
    public void setHash() {
        hasher.update("ClassDef", constructor);
        classFunc.forEach(FuncDef::setHash);
        hash = hasher.hexdigest();
    }

    @Override
    public void printHash() {
        constructor.printHash();
        for(FuncDef f : classFunc){
            System.out.print("  ");
            f.printHash();
        }
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