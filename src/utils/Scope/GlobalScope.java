package src.utils.Scope;

import src.utils.error.MultipleDefinitions;
import src.utils.error.error;
import src.utils.pos.Position;
import src.utils.type.ClassInfo;
import src.utils.type.FuncInfo;
import src.utils.type.Type;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalScope extends Scope {


    public HashMap<String, ClassInfo> ClassList;
    public HashMap<String, Type> GlobalVarList;
    public ArrayList<String> strLiteral = new ArrayList<>();

    public GlobalScope() {
        super();
        ClassList = new HashMap<>();
        GlobalVarList = new HashMap<>();
        FuncInfo print = new FuncInfo(new Type("void"));
        print.args.put("str", new Type("string"));
        FuncList.put("print", print);
        FuncList.put("println", print);

        FuncInfo printint = new FuncInfo(new Type("void"));
        printint.args.put("n", new Type("int"));
        FuncList.put("printInt", printint);
        FuncList.put("printlnInt", printint);

        FuncInfo getstring = new FuncInfo(new Type("string"));
        FuncList.put("getString", getstring);

        FuncInfo tostring = new FuncInfo(new Type("string"));
        tostring.args.put("i", new Type("int"));
        FuncList.put("toString", tostring);

        FuncInfo getInt = new FuncInfo(new Type("int"));
        FuncList.put("getInt", getInt);


        ClassInfo stringclass = new ClassInfo();
        FuncInfo lenfunc = new FuncInfo(new Type("int"));
        stringclass.methods.put("length", new FuncInfo(new Type("int")));
        stringclass.methods.put("parseInt", new FuncInfo(new Type("int")));

        FuncInfo substringfunc = new FuncInfo(new Type("string"));
        substringfunc.args.put("left", new Type("int"));
        substringfunc.args.put("right", new Type("int"));
        stringclass.methods.put("substring", substringfunc);

        FuncInfo ordfunc = new FuncInfo(new Type("int"));
        ordfunc.args.put("pos", new Type("int"));
        stringclass.methods.put("ord", ordfunc);

        ClassList.put("string", stringclass);
    }

    public void addClass(String name, ClassInfo t, Position p) {
        if (ClassList.containsKey(name)) {
            throw new MultipleDefinitions(p);
        }
        ClassList.put(name, t);
    }



    public ClassInfo getClass(String name) {
        if (ClassList.containsKey(name)) return ClassList.get(name);
        return null;
    }

    public void addMemberFunc(String className, String funcName, FuncInfo t, Position p) {
        if (!ClassList.containsKey(className)) {
            throw new error("No such class: " + className, p);
        }
        ClassList.get(className).addMemberFunc(funcName, t, p);
    }

    public void addMemberVar(String className, String varName, Type t, Position p) {
        if (!ClassList.containsKey(className)) {
            throw new error("No such class: " + className, p);
        }
        ClassList.get(className).addMemberVar(varName, t, p);
    }

    public void addGlobalVar(String varName, Type T, Position p) {
        if (GlobalVarList.containsKey(varName)) {
            throw new MultipleDefinitions(p);
        }
        GlobalVarList.put(varName, T);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
