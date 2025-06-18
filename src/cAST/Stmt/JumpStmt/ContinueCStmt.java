package src.cAST.Stmt.JumpStmt;
import src.cAST.BaseCASTNode;
import src.cAST.Stmt.CStmt;

public class ContinueCStmt extends CStmt {
    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
