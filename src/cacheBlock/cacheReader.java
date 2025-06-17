package src.cacheBlock;

import src.cAST.BaseCASTNode;
import src.cAST.CProg;
import src.cAST.Def.ClassDef;
import src.cAST.Def.Constructor;
import src.cAST.Def.FuncDef;
import src.cAST.Def.VarDef;
import src.cAST.Expr.*;
import src.cAST.Stmt.BlockStmt;
import src.cAST.Stmt.EmptyStmt;
import src.cAST.Stmt.ExprStmt;
import src.cAST.Stmt.JumpStmt.BreakStmt;
import src.cAST.Stmt.JumpStmt.ContinueStmt;
import src.cAST.Stmt.JumpStmt.IfStmt;
import src.cAST.Stmt.JumpStmt.ReturnStmt;
import src.cAST.Stmt.LoopStmt.ForStmt;
import src.cAST.Stmt.LoopStmt.WhileStmt;
import src.cAST.Stmt.VarDefStmt;
import src.utils.ByteUtils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

public class cacheReader {
    public ArrayList<cBlock> list = new ArrayList<>();
    ArrayList<BaseCASTNode> cNodes = new ArrayList<>();
    public int curPos = -1;

    static ArrayList<Class<?>> classList = new ArrayList<>();

    static {
        classList.add(CProg.class);

        classList.add(ClassDef.class);
        classList.add(Constructor.class);
        classList.add(FuncDef.class);
        classList.add(VarDef.class);

        classList.add(BlockStmt.class);
        classList.add(EmptyStmt.class);
        classList.add(ExprStmt.class);
        classList.add(BreakStmt.class);
        classList.add(ContinueStmt.class);
        classList.add(IfStmt.class);
        classList.add(ReturnStmt.class);
        classList.add(ForStmt.class);
        classList.add(WhileStmt.class);
        classList.add(VarDefStmt.class);

        classList.add(ArrayAccessExpr.class);
        classList.add(ArrayLiteralExpr.class);
        classList.add(AssignExpr.class);
        classList.add(BinaryArithExpr.class);
        classList.add(BinaryLogicExpr.class);
        classList.add(BoolLiteralExpr.class);
        classList.add(FmtStrLiteralExpr.class);
        classList.add(FuncCallExpr.class);
        classList.add(IntLiteralExpr.class);
        classList.add(MemberFuncCallExpr.class);
        classList.add(MemberObjAccessExpr.class);
        classList.add(NewArrayExpr.class);
        classList.add(NewArrayInitExpr.class);
        classList.add(NewTypeExpr.class);
        classList.add(NullExpr.class);
        classList.add(ParenthesesExpr.class);
        classList.add(RowExpr.class);
        classList.add(StringLiteralExpr.class);
        classList.add(TernaryBranchExpr.class);
        classList.add(ThisPtrExpr.class);
        classList.add(UnaryArithExpr.class);
        classList.add(UnaryLogicExpr.class);
        classList.add(VarExpr.class);
    }

    public cacheReader(DataInputStream dis) throws IOException {
        list = readAllBlocks(dis);
        toAST();
    }

    public ArrayList<cBlock> readAllBlocks(DataInputStream dis) throws IOException {
        ArrayList<cBlock> blocks = new ArrayList<>();
        while (true) {
            try {
                blocks.add(cBlock.readFromStream(dis));
            } catch (EOFException e) {
                break;
            }
        }
        return blocks;
    }

    public void toAST(){
        for(cBlock ccb: list){
            int ind = ByteUtils.byteArrayToInt(ccb.typeHash);
            Class<?> clazz = classList.get(ind);
            if (clazz != null) {
                try {
                    BaseCASTNode cast = (BaseCASTNode) clazz.getDeclaredConstructor().newInstance();
                    cNodes.add(cast);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create instance for type: " + clazz.getName(), e);
                }
            } else {
                throw new RuntimeException("Unknown type index: " + ind);
            }
        }
    }

}
