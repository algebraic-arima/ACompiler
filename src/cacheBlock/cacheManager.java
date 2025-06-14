package src.cacheBlock;

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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class cacheManager implements __ASTVisitor {
    public ArrayList<cBlock> list = new ArrayList<>();
    public int curPos = -1;

    public cacheManager(Prog p, DataOutputStream dos) throws IOException {
        p.accept(this);
        writeCache(dos);
    }

    public void writeCache(DataOutputStream dos) throws IOException {
        for (cBlock c : list) {
            c.writeToStream(dos);
        }
    }

    @Override
    public void visit(Prog node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.defs.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(FuncDef node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.funcBody.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(ClassDef node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.classFunc.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(VarDef node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.initVals.values().forEach(d -> d.accept(this));
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
    public void visit(Constructor node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.funcBody.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(BreakStmt node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);
    }

    @Override
    public void visit(ContinueStmt node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);

    }

    @Override
    public void visit(ReturnStmt node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.retExpr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(IfStmt node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.condition.accept(this);
        node.trueStmt.accept(this);
        node.falseStmt.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(WhileStmt node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.cond.accept(this);
        node.body.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(ForStmt node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
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
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.varDef.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(ExprStmt node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.expr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(BlockStmt node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.stmts.forEach(d ->d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(EmptyStmt node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);
    }

    @Override
    public void visit(ArrayAccessExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.array.accept(this);
        node.index.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(ArrayLiteralExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.elements.forEach(d ->d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(AssignExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.var.accept(this);
        node.value.accept(this);
        curPos = tmp;

    }

    @Override
    public void visit(BinaryArithExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.lhs.accept(this);
        node.rhs.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(BinaryLogicExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.lhs.accept(this);
        node.rhs.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(BoolLiteralExpr node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);
    }

    @Override
    public void visit(FmtStrLiteralExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.exprs.forEach(d ->d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(FuncCallExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.args.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(IntLiteralExpr node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);
    }

    @Override
    public void visit(MemberFuncCallExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.obj.accept(this);
        node.args.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(MemberObjAccessExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.obj.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(NewArrayExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.len.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(NewArrayInitExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.init.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(NewTypeExpr node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);
    }

    @Override
    public void visit(NullExpr node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);
    }

    @Override
    public void visit(ParenthesesExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.expr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(RowExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.exps.forEach(d -> d.accept(this));
        curPos = tmp;
    }

    @Override
    public void visit(StringLiteralExpr node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);
    }

    @Override
    public void visit(TernaryBranchExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.cond.accept(this);
        node.trueBranch.accept(this);
        node.falseBranch.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(ThisPtrExpr node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);
    }

    @Override
    public void visit(UnaryArithExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.expr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(UnaryLogicExpr node) {
        int tmp = curPos;
        cBlock c = new cBlock(node, node.hash, tmp);
        curPos = list.size();
        list.add(c);
        node.expr.accept(this);
        curPos = tmp;
    }

    @Override
    public void visit(VarExpr node) {
        cBlock c = new cBlock(node, node.hash, curPos);
        list.add(c);
    }
}
