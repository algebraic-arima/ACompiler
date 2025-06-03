package src.utils.Scope;

import src.utils.error.MultipleDefinitions;
import src.utils.pos.Position;
import src.utils.type.FuncInfo;
import src.utils.type.Type;

import java.util.HashMap;

public class Scope {

    public boolean isLOOP;

    public HashMap<String, Type> VarList;
    public HashMap<String, FuncInfo> FuncList;
    public HashMap<String, String> renameVarMap;
    // to find the var,
    // if in the scope, find in renameVarMap
    // if not, resort to parent

    public Scope parent;

    public Scope() {
        parent = null;
        VarList = new HashMap<>();
        FuncList = new HashMap<>();
        renameVarMap = new HashMap<>();
    }

    public Scope(Scope p) {
        parent = p;
        isLOOP = p.isLOOP;
        VarList = new HashMap<>();
        FuncList = new HashMap<>();
        renameVarMap = new HashMap<>();
    }

    public void addVar(String name, Type t, Position p) {
        if (VarList.containsKey(name)) {
            throw new MultipleDefinitions(p);
        }
        VarList.put(name, t);
    }

    public class VarInfo {
        public Type t;
        public String alias;

        public VarInfo(Type t, String alias) {
            this.t = t;
            this.alias = alias;
        }
    }

    public VarInfo getVar(String name) {
        if (VarList.containsKey(name)) return new VarInfo(VarList.get(name), renameVarMap.get(name));
        if (parent != null) return parent.getVar(name);
        return null;
    }

    public void addFunc(String name, FuncInfo t, Position p) {
        if (FuncList.containsKey(name)) {
            throw new MultipleDefinitions(p);
        }
        FuncList.put(name, t);
    }

    public FuncInfo getFunc(String name) {
        if (FuncList.containsKey(name))
            return FuncList.get(name);
        if (parent != null)
            return parent.getFunc(name);
        return null;
    }

    public boolean isGlobal() {
        return false;
    }
}
