package src.cAST.Stmt.LoopStmt;

import src.cAST.Expr.Expr;
import src.cAST.Stmt.Stmt;

public class WhileStmt extends Stmt {
    public Expr cond;
    public Stmt body;
}
