package src.cAST.Expr;

public class BinaryArithExpr extends Expr {

    public enum BArithOp {
        ADD, SUB, MUL, DIV, MOD, BLS, BRS, BAND, BOR, BXOR, EQ, NE, LT, GT, LE, GE,
    }// 16 = 10 + 6

    public Expr lhs, rhs;
    public BArithOp op;

    public BinaryArithExpr(Expr lhs, Expr rhs, String op) {
        this.lhs = lhs;
        this.rhs = rhs;
        switch (op) {
            case "+":
                this.op = BArithOp.ADD;
                break;
            case "-":
                this.op = BArithOp.SUB;
                break;
            case "*":
                this.op = BArithOp.MUL;
                break;
            case "/":
                this.op = BArithOp.DIV;
                break;
            case "%":
                this.op = BArithOp.MOD;
                break;
            case "<<":
                this.op = BArithOp.BLS;
                break;
            case ">>":
                this.op = BArithOp.BRS;
                break;
            case "&":
                this.op = BArithOp.BAND;
                break;
            case "|":
                this.op = BArithOp.BOR;
                break;
            case "^":
                this.op = BArithOp.BXOR;
                break;
            case "==":
                this.op = BArithOp.EQ;
                break;
            case "!=":
                this.op = BArithOp.NE;
                break;
            case "<":
                this.op = BArithOp.LT;
                break;
            case ">":
                this.op = BArithOp.GT;
                break;
            case "<=":
                this.op = BArithOp.LE;
                break;
            case ">=":
                this.op = BArithOp.GE;
                break;

        }
    }
}
