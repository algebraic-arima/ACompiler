package src.AST.Dec;

import src.AST.ASTVisitor;
import src.AST.BaseASTNode;
import src.AST.Stmt.BlockStmt;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;
import src.utils.type.Type;

import java.util.ArrayList;

public class GfuncDec extends Dec {
    public String funcName;
    public Type retType;
    public ArrayList<Type> funcParams;

    public GfuncDec(Position p) {
        super(p);
        funcParams = new ArrayList<>();
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
