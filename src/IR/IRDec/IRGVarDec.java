package src.IR.IRDec;

import src.IR.IRVisitor;
import src.utils.IRType.IRType;
import src.utils.type.Type;

public class IRGVarDec extends IRDec{
    public IRType irType;
    public String name;

    public IRGVarDec() {
        irType = new IRType();
        name = "";
    }

    public IRGVarDec(Type type, String name) {
        irType = new IRType(type);
        this.name = name;
    }

    @Override
    public void print() {
        System.out.print(name + " = external global " + irType.typeName + "\n");
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
