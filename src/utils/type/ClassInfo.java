package src.utils.type;

import src.utils.error.error;
import src.utils.pos.Position;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ClassInfo {
    public LinkedHashMap<String, Type> fields;
    public HashMap<String, FuncInfo> methods;

    public ClassInfo() {
        fields = new LinkedHashMap<>();
        methods = new HashMap<>();
    }

    public int getMemberOffset(String memberName) {
        int offset = 0;
        for (String key : fields.keySet()) {
            if (key.equals(memberName)) {
                return offset;
            }
            offset += 1;
        }
        return -1;
    }

    public void addMemberFunc(String funcName, FuncInfo t, Position p) {
        if (methods.containsKey(funcName) || fields.containsKey(funcName)) {
            throw new error("member function name used in class def", p);
        }
        methods.put(funcName, t);
    }

    public void addMemberVar(String varName, Type t, Position p) {
        if (methods.containsKey(varName) || fields.containsKey(varName)) {
            throw new error("member variable name used in class def", p);
        }
        fields.put(varName, t);
    }

}