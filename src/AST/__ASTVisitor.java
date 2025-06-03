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
import src.utils.type.Type;

public interface __ASTVisitor {

    Type voidType = new Type("void");
    Type intType = new Type("int");
    Type boolType = new Type("bool");
    Type stringType = new Type("string");

    void visit(Prog node);

    void visit(FuncDef node);

    void visit(ClassDef node);

    void visit(VarDef node);

    void visit(GvarDec node);

    void visit(GfuncDec node);

    void visit(ClassDec node);

    void visit(MethodDec node);

    void visit(Constructor node);

    void visit(BreakStmt node);

    void visit(ContinueStmt node);

    void visit(ReturnStmt node);

    void visit(IfStmt node);

    void visit(WhileStmt node);

    void visit(ForStmt node);

    void visit(VarDefStmt node);

    void visit(ExprStmt node);

    void visit(BlockStmt node);

    void visit(EmptyStmt node);

    void visit(ArrayAccessExpr node);

    void visit(ArrayLiteralExpr node);

    void visit(AssignExpr node);

    void visit(BinaryArithExpr node);

    void visit(BinaryLogicExpr node);

    void visit(BoolLiteralExpr node);

    void visit(FmtStrLiteralExpr node);

    void visit(FuncCallExpr node);

    void visit(IntLiteralExpr node);

    void visit(MemberFuncCallExpr node);

    void visit(MemberObjAccessExpr node);

    void visit(NewArrayExpr node);

    void visit(NewArrayInitExpr node);

    void visit(NewTypeExpr node);

    void visit(NullExpr node);

    void visit(ParenthesesExpr node);

    void visit(RowExpr node);

    void visit(StringLiteralExpr node);

    void visit(TernaryBranchExpr node);

    void visit(ThisPtrExpr node);

    void visit(UnaryArithExpr node);

    void visit(UnaryLogicExpr node);

    void visit(VarExpr node);
}
