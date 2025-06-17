package src.cAST.Stmt;

import src.cAST.BaseCASTNode;

import java.util.ArrayList;

public class BlockStmt extends Stmt {
    public ArrayList<Stmt> stmts = new ArrayList<>();

    @Override
    void addChild(BaseCASTNode c) {
        if(c instanceof Stmt s){
            stmts.add(s);
            return;
        }
        throw new RuntimeException("AST type not match!");
    }
}
