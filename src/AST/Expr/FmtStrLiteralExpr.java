package src.AST.Expr;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;
import src.utils.type.Type;

import java.util.ArrayList;

public class FmtStrLiteralExpr extends Expr {
    public ArrayList<String> strs;
    public ArrayList<Expr> exprs;

    public FmtStrLiteralExpr(Position p, Type t) {
        super(p);
        type = t;
        strs = new ArrayList<>();
        exprs = new ArrayList<>();
    }

    @Override
    public void setHash() {
        if (hash != null) return;
        hasher.update("FmtStrLiteralExpr", type);
        for (String str : strs) {
            hasher.update(str);
        }
        for (Expr expr : exprs) {
            hasher.update(expr.hash);
        }
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
