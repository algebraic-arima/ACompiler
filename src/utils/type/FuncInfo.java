package src.utils.type;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class FuncInfo {
    public Type retType;
    public LinkedHashMap<String, Type> args;

    public FuncInfo(Type returnType) {
        this.retType = returnType;
        args = new LinkedHashMap<>();
    }

    // for declarations
    public FuncInfo(Type returnType, ArrayList<Type> params) {
        this.retType = returnType;
        args = new LinkedHashMap<>();
        for (int i = 0; i < params.size(); ++i) {
            args.put(String.valueOf(i), params.get(i));
        }
    }
}