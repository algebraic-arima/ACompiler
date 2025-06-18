package src.cacheBlock;

import src.cAST.BaseCASTNode;
import src.cAST.CProg;
import src.cAST.Def.ClassCDef;
import src.cAST.Def.CConstructor;
import src.cAST.Def.FuncCDef;
import src.cAST.Def.VarCDef;
import src.cAST.Expr.*;
import src.cAST.Separator;
import src.cAST.Stmt.BlockCStmt;
import src.cAST.Stmt.EmptyCStmt;
import src.cAST.Stmt.ExprCStmt;
import src.cAST.Stmt.JumpStmt.BreakCStmt;
import src.cAST.Stmt.JumpStmt.ContinueCStmt;
import src.cAST.Stmt.JumpStmt.IfCStmt;
import src.cAST.Stmt.JumpStmt.ReturnCStmt;
import src.cAST.Stmt.LoopStmt.ForCStmt;
import src.cAST.Stmt.LoopStmt.WhileCStmt;
import src.cAST.Stmt.VarDefCStmt;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

public class cacheReader {
    public ArrayList<cBlock> list = new ArrayList<>();
    ArrayList<BaseCASTNode> cNodes = new ArrayList<>();
    public int curPos = -1;
    CProg cProg;

    static ArrayList<Class<?>> classList = new ArrayList<>();

    static {
        classList.add(CProg.class); // 0

        classList.add(ClassCDef.class);
        classList.add(CConstructor.class);
        classList.add(FuncCDef.class);
        classList.add(VarCDef.class); // 4

        classList.add(BlockCStmt.class);
        classList.add(EmptyCStmt.class);
        classList.add(ExprCStmt.class);
        classList.add(BreakCStmt.class);
        classList.add(ContinueCStmt.class);
        classList.add(IfCStmt.class);
        classList.add(ReturnCStmt.class);
        classList.add(ForCStmt.class);
        classList.add(WhileCStmt.class);
        classList.add(VarDefCStmt.class); // 14

        classList.add(ArrayAccessCExpr.class);
        classList.add(ArrayLiteralCExpr.class);
        classList.add(AssignCExpr.class);
        classList.add(BinaryArithCExpr.class);
        classList.add(BinaryLogicCExpr.class);
        classList.add(BoolLiteralCExpr.class);
        classList.add(FmtStrLiteralCExpr.class); // 21
        classList.add(FuncCallCExpr.class);
        classList.add(IntLiteralCExpr.class);
        classList.add(MemberFuncCallCExpr.class);
        classList.add(MemberObjAccessCExpr.class);
        classList.add(NewArrayCExpr.class);
        classList.add(NewArrayInitCExpr.class);
        classList.add(NewTypeCExpr.class);
        classList.add(NullCExpr.class);
        classList.add(ParenthesesCExpr.class); // 30
        classList.add(RowCExpr.class);
        classList.add(StringLiteralCExpr.class);
        classList.add(TernaryBranchCExpr.class);
        classList.add(ThisPtrCExpr.class);
        classList.add(UnaryArithCExpr.class);
        classList.add(UnaryLogicCExpr.class);
        classList.add(VarCExpr.class); // 37

        classList.add(Separator.class); // 38
    }

    public cacheReader(DataInputStream dis) throws IOException {
        list = readAllBlocks(dis);
        CAST();
        cProg = cNodes.getFirst() instanceof CProg ? (CProg) cNodes.getFirst() : null;
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

    public void CAST(){
        for(cBlock ccb: list){
            int ind = ccb.getTypeHash();
            Class<?> clazz = classList.get(ind);
            if (clazz != null) {
                try {
                    BaseCASTNode cast = (BaseCASTNode) clazz.getDeclaredConstructor().newInstance();
                    cast.hash = ccb.getDataHash();
                    cNodes.add(cast);
                    int par = ccb.getPar();
                    if (par >= 0 && par < cNodes.size()) {
                        cNodes.get(par).addChild(cast);
                    } else if (par != -1) {
                        throw new RuntimeException("Parent index out of bounds: " + par);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create instance for type: " + clazz.getName(), e);
                }
            } else {
                throw new RuntimeException("Unknown type index: " + ind);
            }
        }
    }

}
