package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;

public class TernaryBranchExpr extends Expr {

    public Expr cond, trueBranch, falseBranch;

    public TernaryBranchExpr(Position p, Expr c, Expr t, Expr f) {
        super(p);
        cond = c;
        trueBranch = t;
        falseBranch = f;
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("TernaryBranchExpr", type, cond, trueBranch, falseBranch);
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
