package src.IR.IRDef;

import src.IR.IRVisitor;
import src.utils.IRType.IRType;
import src.utils.type.Type;

public class IRGlobalVarDef extends IRDef {

    public IRType irType;
    public String name;
    public long value;

    public IRGlobalVarDef() {
        irType = new IRType();
        name = "";
    }

    public IRGlobalVarDef(Type type, String name) {
        irType = new IRType(type);
        this.name = name;
    }

    @Override
    public void print() {
        System.out.print(name + " = dso_local global " + irType.typeName + " ");
        if (irType.typeName.equals("i32")) {
            System.out.print(value + "\n");
        } else if (irType.typeName.equals("ptr") && value == 0) {
            System.out.print("null\n");
        } else if (irType.typeName.equals("i1")) {
            System.out.print(value != 0 ? "true\n" : "false\n");
        }
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
