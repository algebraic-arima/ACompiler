package src.AST.Expr;

import org.antlr.v4.runtime.tree.TerminalNode;
import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.utils.pos.Position;
import src.utils.type.Type;

import java.util.Objects;

public class BoolLiteralExpr extends Expr {
    public boolean value;

    public BoolLiteralExpr(Position p, boolean v, Type t) {
        super(p);
        type = t;
        value = v;
    }

    public BoolLiteralExpr(TerminalNode node) {
        value = Objects.equals(node.getText(), "true");
    }

    @Override
    public void setHash() {
        hasher.update("BoolLiteralExpr", type, value ? "true" : "false");
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
