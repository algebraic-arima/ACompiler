package src.IR.IRDec;

import src.IR.IRDef.IRBlock;
import src.IR.IRVisitor;
import src.utils.IRType.IRType;

import java.util.ArrayList;

public class IRFuncDec extends IRDec {

    public IRType retType;
    public String name;
    public ArrayList<IRType> paramTypes;

    public IRFuncDec(String name) {
        retType = new IRType();
        this.name = name;
        paramTypes = new ArrayList<>();
    }

    @Override
    public void print(){
        System.out.print("declare " + retType.typeName + " " + name + "(");
        for (int i = 0; i < paramTypes.size(); ++i) {
            paramTypes.get(i).print();
            if (i != paramTypes.size() - 1)
                System.out.print(", ");
        }
        System.out.print(")\n");
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
