package src.AST.Def;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.AST.Stmt.BlockStmt;
import src.utils.pos.Position;
import src.utils.type.Type;

import java.util.Map;

public class Constructor extends Def{
    public String className;
    public BlockStmt funcBody;

    public Constructor(Position p){
        super(p);
    }

    @Override
    public void setHash(){
        if (hash != null) return;
        hasher.update("Constructor", funcBody);
        hash = hasher.hexdigest();
    }

    @Override
    public void printHash() {
        System.out.println(className + " " + hash);
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
