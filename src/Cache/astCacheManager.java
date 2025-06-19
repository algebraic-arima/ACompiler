package src.Cache;

import src.AST.__ASTVisitor;
import src.AST.Dec.ClassDec;
import src.AST.Dec.GfuncDec;
import src.AST.Dec.GvarDec;
import src.AST.Dec.MethodDec;
import src.AST.Def.ClassDef;
import src.AST.Def.Constructor;
import src.AST.Def.FuncDef;
import src.AST.Def.VarDef;
import src.AST.Expr.*;
import src.AST.Prog;
import src.AST.Stmt.BlockStmt;
import src.AST.Stmt.EmptyStmt;
import src.AST.Stmt.ExprStmt;
import src.AST.Stmt.JumpStmt.BreakStmt;
import src.AST.Stmt.JumpStmt.ContinueStmt;
import src.AST.Stmt.JumpStmt.IfStmt;
import src.AST.Stmt.JumpStmt.ReturnStmt;
import src.AST.Stmt.LoopStmt.ForStmt;
import src.AST.Stmt.LoopStmt.WhileStmt;
import src.AST.Stmt.VarDefStmt;

import java.io.*;
import java.util.ArrayList;

public class astCacheManager implements __ASTVisitor {
    public ArrayList<cacheBlock> list = new ArrayList<>();
    String cacheDir = "/home/limike/.mcache/";
    public int curPos = -1;

    public astCacheManager(File f, Prog p) throws IOException {
        p.accept(this);
        cacheDir = cacheDir + f.getName() + "/";
        File file = new File(cacheDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
        }
    }

    public void writeCache() throws IOException {
        FileOutputStream c = new FileOutputStream(cacheDir + "ast.cache");
        DataOutputStream dos = new DataOutputStream(c);
        writeCache(dos);
        dos.close();
        c.close();
    }

    public void writeCache(DataOutputStream dos) throws IOException {
        for (cacheBlock c : list) {
            c.writeToStream(dos);
        }
    }

    @Override
    public void visit(Prog node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(0, node.hash, tmp);
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.defs.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(ClassDef node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(1, node.hash, tmp, node.className);
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.classMem.forEach(d -> d.accept(this));
//        Separator sep = new Separator();
//        cBlock s = new cBlock(38, sep.hash, curPos);
//        list.add(s);
        node.constructor.accept(this);
        node.classFunc.forEach(d -> d.accept(this));
        curPos = tmp;
    }


    @Override
    public void visit(Constructor node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(2, node.hash, tmp, node.className);
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.funcBody.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(FuncDef node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(3, node.hash, tmp, node.funcName);
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.funcBody.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(VarDef node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(4, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.initVals.values().forEach(d -> {
            if (d != null) {
                d.accept(this);
            }
        });
        curPos = tmp;
    }

    @Override
    public void visit(GvarDec node) {

    }

    @Override
    public void visit(GfuncDec node) {

    }

    @Override
    public void visit(ClassDec node) {

    }

    @Override
    public void visit(MethodDec node) {

    }


    @Override
    public void visit(BreakStmt node) {
        cacheBlock c = new cacheBlock(8, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);
    }

    @Override
    public void visit(ContinueStmt node) {
        cacheBlock c = new cacheBlock(9, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);

    }

    @Override
    public void visit(ReturnStmt node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(11, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.retExpr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(IfStmt node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(10, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.condition.accept(this);
        node.trueStmt.accept(this);
        node.falseStmt.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(WhileStmt node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(13, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.cond.accept(this);
        node.body.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(ForStmt node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(12, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.init.accept(this);
        node.cond.accept(this);
        node.update.accept(this);
        node.body.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(VarDefStmt node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(14, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.varDef.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(ExprStmt node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(7, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.expr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(BlockStmt node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(5, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.stmts.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(EmptyStmt node) {
        cacheBlock c = new cacheBlock(6, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);
    }

    @Override
    public void visit(ArrayAccessExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(15, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.array.accept(this);
        node.index.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(ArrayLiteralExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(16, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.elements.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(AssignExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(17, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.var.accept(this);
        node.value.accept(this);
        curPos = tmp;

    }

    @Override
    public void visit(BinaryArithExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(18, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.lhs.accept(this);
        node.rhs.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(BinaryLogicExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(19, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.lhs.accept(this);
        node.rhs.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(BoolLiteralExpr node) {
        cacheBlock c = new cacheBlock(20, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);
    }

    @Override
    public void visit(FmtStrLiteralExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(21, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.exprs.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(FuncCallExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(22, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.args.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(IntLiteralExpr node) {
        cacheBlock c = new cacheBlock(23, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);
    }

    @Override
    public void visit(MemberFuncCallExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(24, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.obj.accept(this);
        node.args.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(MemberObjAccessExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(25, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.obj.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(NewArrayExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(26, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.len.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(NewArrayInitExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(27, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.init.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(NewTypeExpr node) {
        cacheBlock c = new cacheBlock(28, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);
    }

    @Override
    public void visit(NullExpr node) {
        cacheBlock c = new cacheBlock(29, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);
    }

    @Override
    public void visit(ParenthesesExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(30, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.expr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(RowExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(31, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.exps.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(StringLiteralExpr node) {
        cacheBlock c = new cacheBlock(32, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);
    }

    @Override
    public void visit(TernaryBranchExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(33, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.cond.accept(this);
        node.trueBranch.accept(this);
        node.falseBranch.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(ThisPtrExpr node) {
        cacheBlock c = new cacheBlock(34, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);
    }

    @Override
    public void visit(UnaryArithExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(35, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.expr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(UnaryLogicExpr node) {
        int tmp = curPos;
        cacheBlock c = new cacheBlock(36, node.hash, tmp, node.getClass().toString());
        node.cacheInd = curPos = list.size();
        list.add(c);
        node.expr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(VarExpr node) {
        cacheBlock c = new cacheBlock(37, node.hash, curPos, node.getClass().toString());
        node.cacheInd = list.size();
        list.add(c);
    }
}