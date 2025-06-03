package src.IR.IRInst;

import src.AST.Expr.BinaryArithExpr;
import src.IR.IRVisitor;
import src.utils.Entity.Constant;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;
import src.utils.IRType.IRType;

import java.util.HashMap;
import java.util.HashSet;

public class Binary extends IRInst {

    public Entity lhs, rhs;
    public String op;
    public IRType type;

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Binary(BinaryArithExpr.BArithOp op) {
        this.op = switch (op) {
            case ADD -> "add";
            case SUB -> "sub";
            case MUL -> "mul";
            case DIV -> "sdiv";
            case MOD -> "srem";
            case BLS -> "shl";
            case BRS -> "ashr";
            case BAND -> "and";
            case BOR -> "or";
            case BXOR -> "xor";
            default -> null;
        };
    }


    public Binary(String op) {
        this.op = switch (op) {
            case "+" -> "add";
            case "-" -> "sub";
            case "*" -> "mul";
            case "/" -> "sdiv";
            case "%" -> "srem";
            case "<<" -> "shl";
            case ">>" -> "ashr";
            case "&" -> "and";
            case "|" -> "or";
            case "^" -> "xor";
            default -> null;
        };
    }

    public Binary(String op, Entity lhs, Entity rhs, Register dest, IRType type) {
        this.op = switch (op) {
            case "+" -> "add";
            case "-" -> "sub";
            case "*" -> "mul";
            case "/" -> "sdiv";
            case "%" -> "srem";
            case "<<" -> "shl";
            case ">>" -> "ashr";
            case "&" -> "and";
            case "|" -> "or";
            case "^" -> "xor";
            default -> null;
        };
        this.lhs = lhs;
        this.rhs = rhs;
        this.dest = dest;
        this.type = type;
    }

    public void setLhs(long value) {
        this.lhs = new Constant(value);
    }

    public void setLhs(Register register) {
        this.lhs = register;
    }

    public void setRhs(long value) {
        this.rhs = new Constant(value);
    }

    public void setRhs(Register register) {
        this.rhs = register;
    }

    public void setLhs(Entity entity) {
        this.lhs = entity;
    }

    public void setRhs(Entity entity) {
        this.rhs = entity;
    }

    @Override
    public void print() {
        System.out.print("  " + dest.name + " = " + op + " ");
        type.print();
        System.out.print(" ");
        lhs.print();
        System.out.print(", ");
        rhs.print();
        System.out.print("\n");
    }

    @Override
    public HashSet<String> getUse() {
        HashSet<String> d = new HashSet<>();
        if (lhs instanceof Register r) {
            d.add(r.name);
        }
        if (rhs instanceof Register r) {
            d.add(r.name);
        }
        return d;
    }

    @Override
    public String getDef() {
        return dest.name;
    }

    @Override
    public IRInst rename(String suffix, HashMap<Register, Entity> param) {
        Binary n = new Binary(op);
        n.op = op;
        n.type = type;
        if (lhs instanceof Register r) {
            if (!param.containsKey(r)) {
                n.lhs = Register.newReg(type, r.name + suffix);
            } else {
                n.lhs = param.get(r);
            }
        } else {
            n.lhs = lhs;
        }
        if (rhs instanceof Register r) {
            if (!param.containsKey(r)) {
                n.rhs = Register.newReg(type, r.name + suffix);
            } else {
                n.rhs = param.get(r);
            }
        } else {
            n.rhs = rhs;
        }
        n.dest = Register.newReg(type, dest.name + suffix);
        return n;
    }

    @Override
    public void CP(String str, long value) {
        if (lhs instanceof Register r && r.name.equals(str)) {
            lhs = new Constant(value);
        }
        if (rhs instanceof Register r && r.name.equals(str)) {
            rhs = new Constant(value);
        }
    }


}
