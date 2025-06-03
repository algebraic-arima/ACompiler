package src.AST.Dec;

import src.AST.ASTVisitor;
import src.AST.Stmt.BlockStmt;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;


public class ClassDec extends Dec {
    public String className;

    public ClassDec(Position p) {
        super(p);
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
