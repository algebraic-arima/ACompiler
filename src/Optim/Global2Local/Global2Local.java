package src.Optim.Global2Local;

import src.IR.IRBuilder;
import src.IR.IRDef.IRBlock;
import src.IR.IRDef.IRFuncDef;
import src.IR.IRDef.IRGlobalVarDef;
import src.IR.IRInst.*;
import src.IR.IRProg;
import src.utils.Entity.AnonReg;
import src.utils.Entity.Register;
import src.utils.IRType.IRType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Global2Local {

    public IRProg prog;
    public HashMap<String, IRType> globalVarList = new HashMap<>();
    public HashMap<String, HashSet<String>> useList = new HashMap<>();
    public HashMap<IRFuncDef, HashSet<String>> localizeList = new HashMap<>();

    public Global2Local(IRProg prog) {
        this.prog = prog;
        collectGlobal();
        localizeList.keySet().forEach(this::localize);
    }

    public void collectGlobal() {
        for (IRGlobalVarDef v : prog.gVarDefs) {
            globalVarList.put(v.name.substring(1), v.irType);
        }
        for (IRFuncDef f : prog.funcDefs) {
            HashSet<String> uses = new HashSet<>();
            useList.put(f.name, uses);
            HashMap<String, Integer> useNum = new HashMap<>();
            int callCnt = 0;
            for (IRBlock b : f.blocks) {
                for (IRInst i : b.IRInsts) {
                    if (i instanceof Load l && l.src.name.startsWith("@")) {
                        String g = l.src.name.substring(1);
                        uses.add(g);
                        if (useNum.containsKey(g)) {
                            useNum.put(g, useNum.get(g) + 1);
                        } else {
                            useNum.put(g, 1);
                        }
                    } else if (i instanceof Store s && s.dest.name.startsWith("@")) {
                        String g = s.dest.name.substring(1);
                        uses.add(g);
                        if (useNum.containsKey(g)) {
                            useNum.put(g, useNum.get(g) + 1);
                        } else {
                            useNum.put(g, 1);
                        }
                    } else if (i instanceof Call) {
                        ++callCnt;
                    }
                }
            }
            HashSet<String> locVar = new HashSet<>();
            for (Map.Entry<String, Integer> e : useNum.entrySet()) {
                if (e.getValue() >= 0.8 * callCnt) {
                    locVar.add(e.getKey());
                }
            }
            localizeList.put(f, locVar);
        }
    }

    public void localize(IRFuncDef func) {
        HashSet<String> uses = localizeList.get(func);
        if (uses.isEmpty()) return;
        for (String s : uses) {
            IRType t = globalVarList.get(s);
            Register dest = Register.newReg(t, "%" + s + "." + func.name.substring(1));
            func.regCollector.IRInsts.add(new Alloca(t, dest));
        }
        for (IRBlock b : func.blocks) {
            ArrayList<IRInst> tmp = new ArrayList<>();
            if (b.label.equals("entry")) {
                for (String s : uses) {
                    IRType t = globalVarList.get(s);
                    Register src = Register.newReg(t, "@" + s);
                    Register dest = Register.newReg(t, "%" + s + "." + func.name.substring(1));
                    Register tempReg = new AnonReg(t);
                    tmp.add(new Load(t, src, tempReg));
                    tmp.add(new Store(t, tempReg, dest));
                }
            }
            for (int ii = 0; ii < b.IRInsts.size(); ++ii) {
                IRInst i = b.IRInsts.get(ii);
                if (i instanceof Load l && l.src.name.startsWith("@")) {
                    String s = l.src.name.substring(1);
                    if (uses.contains(s)) {
                        IRType t = globalVarList.get(s);
                        l.src = Register.newReg(t, "%" + s + "." + func.name.substring(1));
                    }
                    tmp.add(l);
                } else if (i instanceof Store s && s.dest.name.startsWith("@")) {
                    String str = s.dest.name.substring(1);
                    if (uses.contains(str)) {
                        IRType t = globalVarList.get(str);
                        s.dest = Register.newReg(t, "%" + str + "." + func.name.substring(1));
                    }
                    tmp.add(s);
                } else if ((i instanceof Call c && !IRBuilder.builtinFunc.contains(c.funcName)) ||
                        (i instanceof Ret && !func.name.equals("@main")) || i instanceof Tail) {
                    for (String s : uses) {
                        IRType t = globalVarList.get(s);
                        Register dest = Register.newReg(t, "@" + s);
                        Register src = Register.newReg(t, "%" + s + "." + func.name.substring(1));
                        Register tempReg = new AnonReg(t);
                        tmp.add(new Load(t, src, tempReg));
                        tmp.add(new Store(t, tempReg, dest));
                    }
                    while ((i instanceof Call c && !IRBuilder.builtinFunc.contains(c.funcName)) ||
                            (i instanceof Ret && !func.name.equals("@main")) || i instanceof Tail) {
                        tmp.add(i);
                        ++ii;
                        if (ii == b.IRInsts.size()) {
                            break;
                        }
                        i = b.IRInsts.get(ii);
                    }
                    if (ii != b.IRInsts.size()) {
                        for (String s : uses) {
                            IRType t = globalVarList.get(s);
                            Register src = Register.newReg(t, "@" + s);
                            Register dest = Register.newReg(t, "%" + s + "." + func.name.substring(1));
                            Register tempReg = new AnonReg(t);
                            tmp.add(new Load(t, src, tempReg));
                            tmp.add(new Store(t, tempReg, dest));
                        }
                    }
                    --ii;
                } else {
                    tmp.add(i);
                }
            }
            b.IRInsts = tmp;
        }
    }
}
