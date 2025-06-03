package src.AST;

import src.AST.Dec.*;
import src.AST.Def.ClassDef;
import src.AST.Def.Constructor;
import src.AST.Def.FuncDef;
import src.AST.Def.VarDef;
import src.AST.Expr.*;
import src.AST.Stmt.JumpStmt.*;
import src.AST.Stmt.LoopStmt.*;
import src.AST.Stmt.*;

public interface ASTVisitor<T> {

    T visit(Prog node);

    T visit(FuncDef node);

    T visit(ClassDef node);

    T visit(VarDef node);

    T visit(GvarDec node);

    T visit(GfuncDec node);

    T visit(ClassDec node);

    T visit(MethodDec node);

    T visit(Constructor node);

    T visit(BreakStmt node);

    T visit(ContinueStmt node);

    T visit(ReturnStmt node);

    T visit(IfStmt node);

    T visit(WhileStmt node);

    T visit(ForStmt node);

    T visit(VarDefStmt node);

    T visit(ExprStmt node);

    T visit(BlockStmt node);

    T visit(EmptyStmt node);

    T visit(ArrayAccessExpr node);

    T visit(ArrayLiteralExpr node);

    T visit(AssignExpr node);

    T visit(BinaryArithExpr node);

    T visit(BinaryLogicExpr node);

    T visit(BoolLiteralExpr node);

    T visit(FmtStrLiteralExpr node);

    T visit(FuncCallExpr node);

    T visit(IntLiteralExpr node);

    T visit(MemberFuncCallExpr node);

    T visit(MemberObjAccessExpr node);

    T visit(NewArrayExpr node);

    T visit(NewArrayInitExpr node);

    T visit(NewTypeExpr node);

    T visit(NullExpr node);

    T visit(ParenthesesExpr node);

    T visit(RowExpr node);

    T visit(StringLiteralExpr node);

    T visit(TernaryBranchExpr node);

    T visit(ThisPtrExpr node);

    T visit(UnaryArithExpr node);

    T visit(UnaryLogicExpr node);

    T visit(VarExpr node);
}
