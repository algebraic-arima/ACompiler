package src.cAST.Stmt.JumpStmt;

import src.cAST.Expr.Expr;
import src.cAST.Stmt.Stmt;

public class IfStmt extends Stmt {
    public Expr condition;
    public Stmt trueStmt, falseStmt;
}
