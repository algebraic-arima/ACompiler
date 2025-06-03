package src.AST;

import org.antlr.v4.runtime.tree.TerminalNode;
import src.AST.Dec.*;
import src.AST.Def.*;
import src.AST.Expr.*;
import src.AST.Stmt.*;
import src.AST.Stmt.JumpStmt.*;
import src.AST.Stmt.LoopStmt.*;
import src.parser.MxBaseVisitor;
import src.parser.Mx;
import src.utils.pos.Position;
import src.utils.type.Type;

import io.github.rctcwyvrn.blake3.Blake3;

import src.utils.error.*;

public class ASTBuilder extends MxBaseVisitor<BaseASTNode> {

    Type voidType = new Type("void");
    Type intType = new Type("int");
    Type boolType = new Type("bool");
    Type stringType = new Type("string");

    @Override
    public BaseASTNode visitProg(Mx.ProgContext ctx) {
        Prog prog = new Prog(new Position(ctx));
        for (Mx.DefContext def : ctx.def()) {
            prog.defs.add((Def) visit(def));
        }
        for (Mx.DecContext dec : ctx.dec()) {
            prog.decs.add((Dec) visit(dec));
        }
        return prog;
    }

    @Override
    public BaseASTNode visitDef(Mx.DefContext ctx) {
        if (ctx.funcdef() != null) {
            return visitFuncdef(ctx.funcdef());
        } else if (ctx.classdef() != null) {
            return visitClassdef(ctx.classdef());
        } else {
            return visitVardef(ctx.vardef());
        }
    }

    @Override
    public BaseASTNode visitFuncdef(Mx.FuncdefContext ctx) {
        FuncDef funcDef = new FuncDef(new Position(ctx));
        funcDef.retType = new Type(ctx.type(0));
        funcDef.funcName = ctx.ID(0).getText();
        for (int i = 1; i < ctx.ID().size(); i++) {
            if (funcDef.funcParams.containsKey(ctx.ID(i).getText())) {
                throw new error("multiple definitions in function parameter", new Position(ctx));
            }
            funcDef.funcParams.put(ctx.ID(i).getText(), new Type(ctx.type(i)));
        }
        funcDef.funcBody = (BlockStmt) visit(ctx.stmts());
        return funcDef;
    }

    @Override
    public BaseASTNode visitConstructor(Mx.ConstructorContext ctx) {
        Constructor constructor = new Constructor(new Position(ctx));
        constructor.className = ctx.ID().getText();
        constructor.funcBody = (BlockStmt) visit(ctx.stmts());
        return constructor;
    }

    @Override
    public BaseASTNode visitStmts(Mx.StmtsContext ctx) {
        BlockStmt blockStmt = new BlockStmt(new Position(ctx));
        for (Mx.StmtContext stmt : ctx.stmt()) {
            blockStmt.stmts.add((Stmt) visit(stmt));
        }
        return blockStmt;
    }

    @Override
    public BaseASTNode visitClassdef(Mx.ClassdefContext ctx) {
        ClassDef classDef = new ClassDef(new Position(ctx));
        classDef.className = ctx.ID().getText();
        classDef.constructor = (ctx.constructor() == null) ? null :
                ((Constructor) visit(ctx.constructor()));
        for (Mx.VardefContext tc : ctx.vardef()) {
            classDef.classMem.add((VarDef) visit(tc));
        }
        for (Mx.FuncdefContext fuck : ctx.funcdef()) {
            classDef.classFunc.add((FuncDef) visit(fuck));
        }
        return classDef;
    }

    @Override
    public BaseASTNode visitVardef(Mx.VardefContext ctx) {
        VarDef varDef = new VarDef(new Position(ctx));
        varDef.type = new Type(ctx.type());
        for (Mx.SinglevardefContext sv : ctx.singlevardef()) {
            if (varDef.initVals.containsKey(sv.ID().getText())) {
                throw new error("multiple definitions in one stmt", new Position(ctx));
            }
            varDef.initVals.put(sv.ID().getText(), (sv.expr() == null) ? null : (Expr) visit(sv.expr()));
        }
        return varDef;
    }

    @Override
    public BaseASTNode visitDec(Mx.DecContext ctx) {
        // visit declarations
        if (ctx.gvardec() != null) {
            return visitGvardec(ctx.gvardec());
        }
        if (ctx.gfuncdec() != null) {
            return visitGfuncdec(ctx.gfuncdec());
        }
        if (ctx.classdec() != null) {
            return visitClassdec(ctx.classdec());
        }
        if (ctx.fieldec() != null) {
            throw new error("all member variables are private", new Position(ctx));
//            return visitFieldec(ctx.fieldec());
        }
        if (ctx.methodec() != null) {
            return visitMethodec(ctx.methodec());
        }
        return null;
    }

    @Override
    public BaseASTNode visitGvardec(Mx.GvardecContext ctx) {
        GvarDec g = new GvarDec(new Position(ctx));
        g.type = new Type(ctx.type());
        for (TerminalNode var : ctx.ID())
            g.varName.add(var.getText());
        return g;
    }

    @Override
    public BaseASTNode visitGfuncdec(Mx.GfuncdecContext ctx) {
        GfuncDec g = new GfuncDec(new Position(ctx));
        g.funcName = ctx.ID().getText();
        g.retType = new Type(ctx.type(0));
        for (int i = 1; i < ctx.type().size(); ++i) {
            g.funcParams.add(new Type(ctx.type(i)));
        }
        return g;
    }

    @Override
    public BaseASTNode visitClassdec(Mx.ClassdecContext ctx) {
        ClassDec c = new ClassDec(new Position(ctx));
        c.className = ctx.ID().getText();
        return c;
    }

    @Override
    public BaseASTNode visitMethodec(Mx.MethodecContext ctx) {
        MethodDec m = new MethodDec(new Position(ctx));
        m.className = ctx.ID(0).getText();
        m.funcName = ctx.ID(1).getText();
        m.retType = new Type(ctx.type(0));
        for (int i = 1; i < ctx.type().size(); ++i) {
            m.funcParams.add(new Type(ctx.type(i)));
        }
        return m;
    }

    @Override
    public BaseASTNode visitIfStmt(Mx.IfStmtContext jumping) {
        IfStmt is = new IfStmt(new Position(jumping));
        is.condition = (Expr) visit(jumping.expr());
        is.trueStmt = (Stmt) visit(jumping.truestmt);
        is.falseStmt = (jumping.falsestmt == null) ?
                null : (Stmt) visit(jumping.falsestmt);
        return is;
    }

    @Override
    public BaseASTNode visitBreakStmt(Mx.BreakStmtContext ctx) {
        return new BreakStmt(new Position(ctx));
    }

    @Override
    public BaseASTNode visitContinueStmt(Mx.ContinueStmtContext ctx) {
        return new ContinueStmt(new Position(ctx));
    }

    @Override
    public BaseASTNode visitReturnStmt(Mx.ReturnStmtContext ctx) {
        ReturnStmt rs = new ReturnStmt(new Position(ctx));
        if (ctx.expr() != null) rs.retExpr = (Expr) visit(ctx.expr());
        return rs;
    }

    @Override
    public BaseASTNode visitForStmt(Mx.ForStmtContext ctx) {
        ForStmt fs = new ForStmt(new Position(ctx));
        if (ctx.rowexpr() != null) {
            fs.init = new ExprStmt(new Position(ctx), (Expr) visit(ctx.rowexpr()));
        } else if (ctx.vardef() != null) {
            fs.init = new VarDefStmt(new Position(ctx), (VarDef) visit(ctx.vardef()));
        }
        fs.cond = ctx.cond == null ? null : (Expr) visit(ctx.cond);
        fs.update = ctx.update == null ? null : (Expr) visit(ctx.update);
        fs.body = (Stmt) visit(ctx.stmt());
        return fs;
    }

    @Override
    public BaseASTNode visitWhileStmt(Mx.WhileStmtContext ctx) {
        WhileStmt ws = new WhileStmt(new Position(ctx));
        ws.cond = (Expr) visit(ctx.expr());
        ws.body = (Stmt) visit(ctx.stmt());
        return ws;
    }

    @Override
    public BaseASTNode visitBlockStmt(Mx.BlockStmtContext ctx) {
        return visit(ctx.stmts());
    }

    @Override
    public BaseASTNode visitVarDefStmt(Mx.VarDefStmtContext ctx) {
        return new VarDefStmt(new Position(ctx), (VarDef) visit(ctx.vardef()));
    }

    @Override
    public BaseASTNode visitExprStmt(Mx.ExprStmtContext ctx) {
        return new ExprStmt(new Position(ctx), (Expr) visit(ctx.expr()));
    }

    @Override
    public BaseASTNode visitLoopStmt(Mx.LoopStmtContext ctx) {
        return visit(ctx.lpstmt());
    }

    @Override
    public BaseASTNode visitJumpStmt(Mx.JumpStmtContext ctx) {
        return visit(ctx.jmpstmt());
    }

    @Override
    public BaseASTNode visitEmptyStmt(Mx.EmptyStmtContext ctx) {
        return new EmptyStmt(new Position(ctx));
    }

    @Override
    public BaseASTNode visitMemberFuncCall(Mx.MemberFuncCallContext ctx) {
        MemberFuncCallExpr mfce = new MemberFuncCallExpr(new Position(ctx),
                (Expr) visit(ctx.expr()),
                ctx.ID().getText());
        mfce.args = (ctx.rowexpr() == null)
                ? new RowExpr(new Position(ctx))
                : (RowExpr) visit(ctx.rowexpr());
        return mfce;
    }

    @Override
    public BaseASTNode visitMemberObjAccess(Mx.MemberObjAccessContext ctx) {
        return new MemberObjAccessExpr(new Position(ctx),
                (Expr) visit(ctx.expr()),
                ctx.ID().getText());
    }

    @Override
    public BaseASTNode visitArrayAccess(Mx.ArrayAccessContext ctx) {
        return new ArrayAccessExpr(new Position(ctx),
                (Expr) visit(ctx.expr(0)),
                (Expr) visit(ctx.expr(1)));
    }

    @Override
    public BaseASTNode visitFuncCall(Mx.FuncCallContext ctx) {
        FuncCallExpr fc = new FuncCallExpr(new Position(ctx),
                ctx.ID().getText());
        fc.args = (ctx.rowexpr() == null)
                ? new RowExpr(new Position(ctx))
                : (RowExpr) visit(ctx.rowexpr());
        return fc;
    }

    @Override
    public BaseASTNode visitRUnaryExp(Mx.RUnaryExpContext ctx) {
        return new UnaryArithExpr(new Position(ctx),
                (Expr) visit(ctx.expr()), ctx.op.getText(), false);
    }

    @Override
    public BaseASTNode visitLUnaryExp(Mx.LUnaryExpContext ctx) {
        String op = ctx.op.getText();
        Expr expr = (Expr) visit(ctx.expr());
        if (op.equals("++") || op.equals("--") || op.equals("~") || op.equals("-")) {
            return new UnaryArithExpr(new Position(ctx), expr, op, true);
        } else if (op.equals("!")) {
            return new UnaryLogicExpr(new Position(ctx), expr);
        } else {
            return null;
        }
    }

    @Override
    public BaseASTNode visitBinaryExp(Mx.BinaryExpContext ctx) {
        String op = ctx.op.getText();
        Expr expr1 = (Expr) visit(ctx.expr(0));
        Expr expr2 = (Expr) visit(ctx.expr(1));
        if (op.equals("<") || op.equals(">") || op.equals("<=") ||
                op.equals(">=") || op.equals("==") || op.equals("!=")
                || op.equals("+") || op.equals("-") || op.equals("*")
                || op.equals("/") || op.equals("%") || op.equals("<<")
                || op.equals(">>") || op.equals("&") || op.equals("|")
                || op.equals("^")) {
            return new BinaryArithExpr(new Position(ctx), expr1, expr2, op);
        } else if (op.equals("&&") || op.equals("||")) {
            return new BinaryLogicExpr(new Position(ctx), expr1, expr2, op);
        } else {
            return null;
        }
    }

    @Override
    public BaseASTNode visitAssignExp(Mx.AssignExpContext ctx) {
        return new AssignExpr(new Position(ctx),
                (Expr) visit(ctx.expr(0)),
                (Expr) visit(ctx.expr(1)));
    }

    @Override
    public BaseASTNode visitParentheses(Mx.ParenthesesContext ctx) {
        return new ParenthesesExpr(new Position(ctx), (Expr) visit(ctx.expr()));
    }

    @Override
    public BaseASTNode visitTernaryExp(Mx.TernaryExpContext ctx) {
        return new TernaryBranchExpr(new Position(ctx),
                (Expr) visit(ctx.expr(0)),
                (Expr) visit(ctx.expr(1)),
                (Expr) visit(ctx.expr(2)));
    }


    @Override
    public BaseASTNode visitNewArray(Mx.NewArrayContext ctx) {
        int dim = ctx.LBKT().size();
        if (dim == 0) {
            return new NewTypeExpr(new Position(ctx),
                    new Type(ctx.singletype().getText()));
        }
        if (ctx.arrayliteral() == null) {
            // no init
            NewArrayExpr n = new NewArrayExpr(new Position(ctx));
            n.type = new Type(ctx.singletype().getText(), dim);
            for (Mx.ExprContext e : ctx.expr()) {
                n.len.add((Expr) visit(e));
            }
            n.dim = dim;
            return n;
        } else if (ctx.expr().isEmpty() && ctx.arrayliteral() != null) {
            NewArrayInitExpr n = new NewArrayInitExpr(new Position(ctx));
            n.type = new Type(ctx.singletype().getText(), dim);
            n.init = (ArrayLiteralExpr) visit(ctx.arrayliteral());
            return n;
        }
        // throw sth.
        return null;
    }

    @Override
    public BaseASTNode visitNewClass(Mx.NewClassContext ctx) {
        return new NewTypeExpr(new Position(ctx),
                new Type(ctx.ID().getText()));
    }

    @Override
    public BaseASTNode visitThisPtr(Mx.ThisPtrContext ctx) {
        return new ThisPtrExpr(new Position(ctx));
    }

    @Override
    public BaseASTNode visitVarAccess(Mx.VarAccessContext ctx) {
        return new VarExpr(new Position(ctx), ctx.ID().getText());
    }

    @Override
    public BaseASTNode visitConstant(Mx.ConstantContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public BaseASTNode visitFormatString(Mx.FormatStringContext ctx) {
        return visit(ctx.fmtstr());
    }

    @Override
    public BaseASTNode visitLiteral(Mx.LiteralContext ctx) {
        if (ctx.INTCONST() != null) {
            return new IntLiteralExpr(new Position(ctx), Long.parseLong(ctx.INTCONST().getText()), intType);
        }
        if (ctx.TRUE() != null || ctx.FALSE() != null) {
            return new BoolLiteralExpr(new Position(ctx), ctx.TRUE() != null, boolType);
        }
        if (ctx.STRINGCONST() != null) {
            String val = ctx.STRINGCONST().getText();
            val = val.substring(1, val.length() - 1);
            val = val.replace("\\\\", "\\");
            val = val.replace("\\\"", "\"");
            val = val.replace("\\n", "\n");
            return new StringLiteralExpr(new Position(ctx), val, stringType);
        }
        if (ctx.NULL() != null) {
            return new NullExpr(new Position(ctx));
        }
        if (ctx.arrayliteral() != null) {
            return visitArrayliteral(ctx.arrayliteral());
        }
        return null;
    }


    @Override
    public BaseASTNode visitRowexpr(Mx.RowexprContext ctx) {
        RowExpr re = new RowExpr(new Position(ctx));
        for (int i = 0; i < ctx.expr().size(); i++) {
            if (ctx.expr(i) != null) re.exps.add((Expr) visit(ctx.expr(i)));
        }
        return re;
    }

    @Override
    public BaseASTNode visitArrayliteral(Mx.ArrayliteralContext ctx) {
        ArrayLiteralExpr ale = new ArrayLiteralExpr(new Position(ctx));
        if (ctx.rowexpr() == null)
            return ale;
        for (Mx.ExprContext e : ctx.rowexpr().expr()) {
            ale.elements.add((Expr) visit(e));
        }
        return ale;
    }

    @Override
    public BaseASTNode visitFmtstr(Mx.FmtstrContext ctx) {
        if (ctx.FMTSTRPURE() != null) {
            String val = ctx.FMTSTRPURE().getText();
            val = val.substring(2, val.length() - 1);
            val = val.replace("$$", "$");

            val = val.replace("\\\\", "\\");
            val = val.replace("\\\"", "\"");
            val = val.replace("\\n", "\n");
            return new StringLiteralExpr(new Position(ctx), val, stringType);
        }
        // with expr
        FmtStrLiteralExpr f = new FmtStrLiteralExpr(new Position(ctx), stringType);
        String bgn = ctx.FMTSTRBGN().getText();
        bgn = bgn.substring(2, bgn.length() - 1);
        bgn = bgn.replace("$$", "$");
        f.strs.add(bgn);
        for (int i = 0; i < ctx.FMTSTRBODY().size(); ++i) {
            String body = ctx.FMTSTRBODY(i).getText();
            body = body.substring(1, body.length() - 1);

            body = body.replace("\\\\", "\\");
            body = body.replace("\\\"", "\"");
            body = body.replace("\\n", "\n");
            body = body.replace("$$", "$");
            f.strs.add(body);
        }
        String end = ctx.FMTSTREND().getText();
        end = end.substring(1, end.length() - 1);
        end = end.replace("$$", "$");
        f.strs.add(end);
        for (int i = 0; i < ctx.expr().size(); ++i) {
            f.exprs.add((Expr) visit(ctx.expr(i)));
        }
        return f;
    }


}
