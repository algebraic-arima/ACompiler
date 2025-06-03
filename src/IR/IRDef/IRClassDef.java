package src.IR.IRDef;

import src.AST.Def.VarDef;
import src.IR.IRVisitor;
import src.utils.IRType.IRType;

import java.util.ArrayList;

public class IRClassDef extends IRDef {

    public String className;
    public ArrayList<IRType> fields;

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public IRClassDef (String className){
        this.className = "%class." + className;
        this.fields = new ArrayList<>();
    }

    public int getFieldNum(){
        return fields.size();
    }

    @Override
    public void print() {
        System.out.print(className + " = type {");
        for (int i = 0; i < fields.size(); ++i) {
            fields.get(i).print();
            if (i != fields.size() - 1)
                System.out.print(", ");
        }
        System.out.println("}");
    }
}
