package src.Semantic;

import src.AST.Dec.*;
import src.AST.__ASTVisitor;
import src.AST.Def.ClassDef;
import src.AST.Def.Constructor;
import src.AST.Def.FuncDef;
import src.AST.Def.VarDef;
import src.AST.Expr.*;
import src.AST.Prog;
import src.AST.Stmt.*;
import src.AST.Stmt.JumpStmt.BreakStmt;
import src.AST.Stmt.JumpStmt.ContinueStmt;
import src.AST.Stmt.JumpStmt.IfStmt;
import src.AST.Stmt.JumpStmt.ReturnStmt;
import src.AST.Stmt.LoopStmt.ForStmt;
import src.AST.Stmt.LoopStmt.WhileStmt;
import src.utils.Scope.GlobalScope;
import src.utils.error.error;
import src.utils.type.ClassInfo;
import src.utils.type.FuncInfo;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolCollector implements __ASTVisitor {

    public GlobalScope gScope;
    public String curClass = null;
    public HashSet<String> symbols;
    // if in class, take down var and func
    // if not, take down func and class only

    public SymbolCollector(GlobalScope g) {
        gScope = g;
        symbols = new HashSet<>();
    }

    @Override
    public void visit(Prog node) {
        node.decs.forEach(d -> d.accept(this));
        node.defs.forEach(d -> d.accept(this));
    }

    @Override
    public void visit(FuncDef node) {
        FuncInfo ft = new FuncInfo(node.retType);
        ft.args = new LinkedHashMap<>(node.funcParams);
        if (curClass == null)
            gScope.addFunc(node.funcName, ft, node.pos);
        else
            gScope.addMemberFunc(curClass, node.funcName, ft, node.pos);
        if (curClass == null) {
            if (symbols.contains(node.funcName)) {
                throw new error("function name used", node.pos);
            } else {
                symbols.add(node.funcName);
            }
        }
    }

    @Override
    public void visit(ClassDef node) {
        gScope.addClass(node.className, new ClassInfo(), node.pos);
        if (symbols.contains(node.className)) {
            throw new error("class name used", node.pos);
        } else {
            symbols.add(node.className);
        }
        if (curClass == null) curClass = node.className;
        else throw new error("nested class definition", node.pos); // guaranteed by g4
        node.classFunc.forEach(d -> d.accept(this));
        node.classMem.forEach(d -> d.accept(this));
        curClass = null;
    }

    @Override
    public void visit(VarDef node) {
        if (curClass != null) {
            for (Map.Entry<String, Expr> entry : node.initVals.entrySet()) {
                gScope.addMemberVar(curClass, entry.getKey(), node.type, node.pos);
            }
        } else {
            for (Map.Entry<String, Expr> entry : node.initVals.entrySet()) {
                if (symbols.contains(entry.getKey())) {
                    throw new error("variable name used", node.pos);
                } else {
                    symbols.add(entry.getKey());
                }
                gScope.addGlobalVar(entry.getKey(), node.type, node.pos);
            }
        }
    }

    @Override
    public void visit(GvarDec node) {
        assert curClass == null;
        for (String s : node.varName) {
            if (symbols.contains(s)) {
                throw new error("variable name used", node.pos);
            } else {
                symbols.add(s);
            }
            gScope.addGlobalVar(s, node.type, node.pos);
        }
    }

    @Override
    public void visit(GfuncDec node) {
        FuncInfo ft = new FuncInfo(node.retType, node.funcParams);
        assert curClass == null;
        gScope.addFunc(node.funcName, ft, node.pos);
        if (symbols.contains(node.funcName)) {
            throw new error("function name used", node.pos);
        } else {
            symbols.add(node.funcName);
        }
    }

    @Override
    public void visit(ClassDec node) {
        if (!gScope.ClassList.containsKey(node.className)) {
            ClassInfo ct = new ClassInfo();
            gScope.ClassList.put(node.className, ct);
        }
    }

    @Override
    public void visit(MethodDec node) {
        ClassInfo ct;
        if (!gScope.ClassList.containsKey(node.className)) {
            ct = new ClassInfo();
            gScope.ClassList.put(node.className, ct);
        } else {
            ct = gScope.ClassList.get(node.className);
        }
        FuncInfo ft = new FuncInfo(node.retType, node.funcParams);
        ct.addMemberFunc(node.funcName, ft, node.pos);
    }

    @Override
    public void visit(Constructor node) {

    }

    @Override
    public void visit(BreakStmt node) {

    }

    @Override
    public void visit(ContinueStmt node) {

    }

    @Override
    public void visit(ReturnStmt node) {

    }

    @Override
    public void visit(IfStmt node) {

    }

    @Override
    public void visit(WhileStmt node) {

    }

    @Override
    public void visit(ForStmt node) {

    }

    @Override
    public void visit(VarDefStmt node) {

    }

    @Override
    public void visit(ExprStmt node) {

    }

    @Override
    public void visit(BlockStmt node) {

    }

    @Override
    public void visit(EmptyStmt node) {

    }

    @Override
    public void visit(ArrayAccessExpr node) {

    }

    @Override
    public void visit(ArrayLiteralExpr node) {

    }

    @Override
    public void visit(AssignExpr node) {

    }

    @Override
    public void visit(BinaryArithExpr node) {

    }

    @Override
    public void visit(BinaryLogicExpr node) {

    }

    @Override
    public void visit(BoolLiteralExpr node) {

    }

    @Override
    public void visit(FmtStrLiteralExpr node) {

    }

    @Override
    public void visit(FuncCallExpr node) {

    }

    @Override
    public void visit(IntLiteralExpr node) {

    }

    @Override
    public void visit(MemberFuncCallExpr node) {

    }

    @Override
    public void visit(MemberObjAccessExpr node) {

    }

    @Override
    public void visit(NewArrayExpr node) {

    }

    @Override
    public void visit(NewArrayInitExpr node) {

    }

    @Override
    public void visit(NewTypeExpr node) {

    }

    @Override
    public void visit(NullExpr node) {

    }

    @Override
    public void visit(ParenthesesExpr node) {

    }

    @Override
    public void visit(RowExpr node) {

    }

    @Override
    public void visit(StringLiteralExpr node) {

    }

    @Override
    public void visit(TernaryBranchExpr node) {

    }

    @Override
    public void visit(ThisPtrExpr node) {

    }

    @Override
    public void visit(UnaryArithExpr node) {

    }

    @Override
    public void visit(UnaryLogicExpr node) {

    }

    @Override
    public void visit(VarExpr node) {

    }

}
