package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.Entity.Register;
import src.utils.pos.Position;

public class VarExpr extends Expr {
    public String varName;
    public Register addr;

    public VarExpr(Position p, String v) {
        super(p);
        varName = v;
    }

    @Override
    public void setHash() {
        hasher.update("VarExpr", type, varName);
        hash = hasher.hexdigest();
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
