package src.AST.Stmt;

import src.AST.ASTVisitor;
import src.AST.__ASTVisitor;
import src.AST.Def.VarDef;
import src.utils.pos.Position;

public class VarDefStmt extends Stmt {

    public VarDef varDef;

    public VarDefStmt(Position p, VarDef v) {
        super(p);
        varDef = v;
    }

    @Override
    public void setHash() {
        hasher.update(varDef);
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
