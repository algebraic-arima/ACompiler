package src.utils.IRType;

import src.utils.type.Type;

public class IRType {
    public String typeName;

    public void print() {
        System.out.print(typeName);
    }

    public IRType() {
        typeName = "void";
    }

    public IRType(Type t) {
        if (t.dim >= 1) {
            typeName = "ptr";
        } else {
            switch (t.atomType) {
                case INT:
                    typeName = "i32";
                    break;
                case BOOL:
                    typeName = "i1";
                    break;
                case STRING:
                    typeName = "ptr";
                    break;
                case VOID:
                    typeName = "void";
                    break;
                case CLASS:
                    typeName = "ptr"; // as a parameter
                    break;
            }
        }
    }

    public IRType(String s) {
        switch (s) {
            case "ptr", "string":
                typeName = "ptr";
                break;
            case "int":
                typeName = "i32";
                break;
            case "bool":
                typeName = "i1";
                break;
            case "void":
                typeName = "void";
                break;
            default:
                typeName = "%class." + s;// on how to interpret a ptr
                break;
        }
    }

    public IRType setType(Type.T atomType) {
        switch (atomType) {
            case INT:
                typeName = "i32";
                break;
            case BOOL:
                typeName = "i1";
                break;
            case STRING:
                typeName = "ptr";
                break;
            case VOID:
                typeName = "void";
                break;
        }
        return this;
    }
}
