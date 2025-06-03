// Generated from /home/limike/Git/compiler-2025/src/parser/Mx.g4 by ANTLR 4.13.2

    package src.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Mx}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MxVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Mx#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(Mx.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDef(Mx.DefContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#funcdef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncdef(Mx.FuncdefContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#classdef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassdef(Mx.ClassdefContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#vardef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVardef(Mx.VardefContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#dec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDec(Mx.DecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#gvardec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGvardec(Mx.GvardecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#gfuncdec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGfuncdec(Mx.GfuncdecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#classdec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassdec(Mx.ClassdecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#fieldec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldec(Mx.FieldecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#methodec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodec(Mx.MethodecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#singlevardef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSinglevardef(Mx.SinglevardefContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#constructor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructor(Mx.ConstructorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#stmts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmts(Mx.StmtsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link Mx#jmpstmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(Mx.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BreakStmt}
	 * labeled alternative in {@link Mx#jmpstmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(Mx.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ContinueStmt}
	 * labeled alternative in {@link Mx#jmpstmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(Mx.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link Mx#jmpstmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(Mx.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link Mx#lpstmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(Mx.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link Mx#lpstmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(Mx.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BlockStmt}
	 * labeled alternative in {@link Mx#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStmt(Mx.BlockStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarDefStmt}
	 * labeled alternative in {@link Mx#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDefStmt(Mx.VarDefStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link Mx#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(Mx.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LoopStmt}
	 * labeled alternative in {@link Mx#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopStmt(Mx.LoopStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code JumpStmt}
	 * labeled alternative in {@link Mx#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJumpStmt(Mx.JumpStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EmptyStmt}
	 * labeled alternative in {@link Mx#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmptyStmt(Mx.EmptyStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MemberFuncCall}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberFuncCall(Mx.MemberFuncCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RUnaryExp}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRUnaryExp(Mx.RUnaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Constant}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(Mx.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarAccess}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarAccess(Mx.VarAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MemberObjAccess}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberObjAccess(Mx.MemberObjAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BinaryExp}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExp(Mx.BinaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ThisPtr}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisPtr(Mx.ThisPtrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FuncCall}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCall(Mx.FuncCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignExp}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignExp(Mx.AssignExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NewArray}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewArray(Mx.NewArrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TernaryExp}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernaryExp(Mx.TernaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayAccess}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayAccess(Mx.ArrayAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FormatString}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormatString(Mx.FormatStringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LUnaryExp}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLUnaryExp(Mx.LUnaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NewClass}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewClass(Mx.NewClassContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Parentheses}
	 * labeled alternative in {@link Mx#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentheses(Mx.ParenthesesContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#rowexpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRowexpr(Mx.RowexprContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(Mx.TypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BaseType}
	 * labeled alternative in {@link Mx#singletype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBaseType(Mx.BaseTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ClassType}
	 * labeled alternative in {@link Mx#singletype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassType(Mx.ClassTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#arraytype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArraytype(Mx.ArraytypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(Mx.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#arrayliteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayliteral(Mx.ArrayliteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link Mx#fmtstr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFmtstr(Mx.FmtstrContext ctx);
}