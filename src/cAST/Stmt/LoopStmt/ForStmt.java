package src.cAST.Stmt.LoopStmt;

import src.cAST.Expr.Expr;
import src.cAST.Stmt.Stmt;

public class ForStmt extends Stmt {
    public Expr cond, update;
    public Stmt init, body;
}
