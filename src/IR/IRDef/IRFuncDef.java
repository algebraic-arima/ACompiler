package src.IR.IRDef;

import src.IR.IRInst.IRInst;
import src.IR.IRVisitor;
import src.utils.IRType.IRType;
import src.IR.IRInst.Alloca;

import java.util.ArrayList;

public class IRFuncDef extends IRDef {

    public IRType retType;
    public String name;
    public ArrayList<IRType> paramTypes;
    public ArrayList<String> paramNames;
    public IRBlock regCollector;
    // collect the vars of Alloca
    public ArrayList<IRBlock> blocks;
    public int stackSize = 0;
    public int funcParamMax = 0;
    public int regNum = 0;
    public boolean hasCall = false;

    public IRFuncDef(String name) {
        retType = new IRType();
        this.name = name;
        paramTypes = new ArrayList<>();
        paramNames = new ArrayList<>();
        blocks = new ArrayList<>();
        regCollector = new IRBlock("entry");
    }

    public void addBlock(IRBlock e) {
        blocks.add(e);
    }

    public void addAlloca(Alloca a) {
        regCollector.addInst(a);
    }

    @Override
    public void print() {
        System.out.print("\ndefine dso_local " + retType.typeName + " " + name + "(");
        for (int i = 0; i < paramNames.size(); ++i) {
            paramTypes.get(i).print();
            System.out.print(" " + paramNames.get(i));
            if (i != paramNames.size() - 1)
                System.out.print(", ");
        }
        System.out.print(") {\n");
        System.out.print("entry:\n");
        regCollector.print();
        blocks.forEach(IRBlock::print);
        System.out.print("}\n");
        System.out.print("; " + stackSize + " * 4 stack memory used\n");
        System.out.print("; at most " + funcParamMax + " * 4 function parameters\n");
    }

    public void reformat() {
//        blocks.removeIf(e -> e.IRInsts.isEmpty() && e.phis.isEmpty());
        // mem2reg will remove empty and useless blocks
        blocks.forEach(e -> {
            e.reformat();
//            regNum += e.regNum;
            if (e.funcParamMax > funcParamMax) {
                funcParamMax = e.funcParamMax;
            }
        });
    }

    public void addAlloca(){
        ArrayList<IRInst> tmp = new ArrayList<>(regCollector.IRInsts);
        tmp.addAll(blocks.getFirst().IRInsts);
        blocks.getFirst().IRInsts = tmp;
        regCollector.IRInsts.clear();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
