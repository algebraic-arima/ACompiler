package src.cAST.Stmt;

import src.cAST.BaseCASTNode;

import java.util.ArrayList;

public class BlockCStmt extends CStmt {
    public ArrayList<CStmt> CStmts = new ArrayList<>();

    @Override
    public void addChild(BaseCASTNode c) {
        if(c instanceof CStmt s){
            CStmts.add(s);
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
