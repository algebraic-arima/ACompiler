package src.cAST.Stmt;

import src.cAST.BaseCASTNode;

public class EmptyCStmt extends CStmt {
    @Override
    public void addChild(BaseCASTNode c) {
        throw new RuntimeException("AST type not match!");
    }
}
