package src.cAST.Expr;

public class MemberFuncCallExpr extends Expr {
    public Expr obj;
    public String funcName;
    public RowExpr args;
}
