package src.Optim.SCCP;

import src.IR.IRDef.IRBlock;
import src.IR.IRDef.IRFuncDef;
import src.IR.IRInst.*;
import src.IR.IRInst.Phi;
import src.utils.Entity.Constant;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;
import src.utils.IRType.IRType;

import java.util.*;

import static src.Optim.SCCP.Var.*;

public class FuncSCCP {

    IRFuncDef func;
    ArrayDeque<SCCPNode> workList;
    HashMap<String, Var> varList;
    HashMap<String, Blk> blockList;
    HashSet<IRBlock> trblk = new HashSet<>();
    HashSet<String> trdef = new HashSet<>();
    HashMap<String, HashMap<String, Edge>> edges = new HashMap<>();
    // succ -> (pred -> edge)

    public FuncSCCP(IRFuncDef _func) {
        func = _func;
        varList = new HashMap<>();
        blockList = new HashMap<>();
        workList = new ArrayDeque<>();
//        func.blocks.forEach(d -> blockList.put(d.label, new Blk(d)));
        collect();
        propagate();
        trim();
        reformat();
    }

    public void collect() {
        for (int i = 0; i < func.paramNames.size(); ++i) {
            String s = func.paramNames.get(i);
            IRType t = func.paramTypes.get(i);
            Var v = new Var(s, t);
            varList.put(s, v);
            v.p = M;
        }
        for (IRBlock b : func.blocks) {
            edges.put(b.label, new HashMap<>());
            b.instList.clear();
            b.instList.addAll(b.phis.values());
            b.instList.addAll(b.IRInsts);
            blockList.put(b.label, new Blk(b));
            for (IRInst i : b.instList) {
                String def = i.getDef();
                if (def != null) {
                    varList.put(def, new Var(def, i.dest.type));
                }
            }
        }
        for (IRBlock b : func.blocks) {
            for (IRInst i : b.instList) {
                for (String str : i.getUse()) {
                    if (varList.containsKey(str)) {
                        varList.get(str).use.put(i, b);
                    }
                }
                if (i instanceof terminalIRInst) {
                    Blk t = blockList.get(b.label);
                    if (i instanceof Br br) {
                        Blk tr = blockList.get(br.trueBlock.label);
                        Blk fa = blockList.get(br.falseBlock.label);
                        tr.pred.add(t);
                        fa.pred.add(t);
                        t.succ.add(tr);
                        t.succ.add(fa);
                        edges.get(br.trueBlock.label).put(b.label, new Edge(b, br.trueBlock));
                        edges.get(br.falseBlock.label).put(b.label, new Edge(b, br.falseBlock));
                    } else if (i instanceof Jmp j) {
                        Blk n = blockList.get(j.block.label);
                        n.pred.add(t);
                        t.succ.add(n);
                        edges.get(j.block.label).put(b.label, new Edge(b, j.block));
                    }
                }
            }
        }
    }

    public void propagate() {
        Blk ent = blockList.get("entry");
        workList.add(ent);
        ent.exec = true;
        while (!workList.isEmpty()) {
            SCCPNode n = workList.poll();
            if (n instanceof Blk b) {
                for (IRInst i : b.blk.instList) {
                    if (i instanceof Binary bin) {
                        meet(getVar(bin.dest), getVar(bin.lhs), bin.op, getVar(bin.rhs));
                    } else if (i instanceof Icmp bin) {
                        meet(getVar(bin.dest), getVar(bin.lhs), bin.op, getVar(bin.rhs));
                    } else if (i instanceof Load) {
                        setM(i.dest);
                    } else if (i instanceof Call c) {
                        if (c.dest != null) {
                            setM(c.dest);
                        }
                    } else if (i instanceof Phi p) {
                        handlePhi(b.blk, p);
                    } else if (i instanceof Br br) {
                        handleBr(b.blk, br);
                    } else if (i instanceof Jmp j) {
                        Blk next = blockList.get(j.block.label);
                        setExec(next);
                        setPass(b.blk.label, j.block.label);
                    } else if (i instanceof GetElePtr) {
                        setM(i.dest);
                    }
                }
            } else if (n instanceof Var v) {
                for (var d : v.use.entrySet()) {
                    IRInst i = d.getKey();
                    IRBlock b = d.getValue();
                    if(blockList.get(b.label).exec) {
                        if (i instanceof Binary bin) {
                            meet(getVar(bin.dest), getVar(bin.lhs), bin.op, getVar(bin.rhs));
                        } else if (i instanceof Icmp bin) {
                            meet(getVar(bin.dest), getVar(bin.lhs), bin.op, getVar(bin.rhs));
                        } else if (i instanceof Load) {
                            setM(i.dest);
                        } else if (i instanceof Call c) {
                            if (c.dest != null) {
                                setM(c.dest);
                            }
                        } else if (i instanceof Phi p) {
                            handlePhi(b, p);
                        } else if (i instanceof Br br) {
                            handleBr(b, br);
                        } else if (i instanceof GetElePtr) {
                            setM(i.dest);
                        }
                    }
                }
            }
        }
    }

    public void trim() {
        for (var b : func.blocks) {
            if (!blockList.get(b.label).exec) {
                trblk.add(b);
            }
        }
        for (var b : varList.entrySet()) {
            if (b.getValue().p == C) {
                trdef.add(b.getKey());
            }
        }
        func.blocks.removeIf(s -> !blockList.get(s.label).exec);
        func.blocks.forEach(b -> {
            b.instList.forEach(i -> {
                if (i instanceof Phi p) {
                    for (int cnt = 0; cnt < p.valList.size(); cnt++) {
                        var e = p.valList.get(cnt);
                        if (!blockList.get(e.b.label).exec) {
                            p.valList.set(cnt, null);
                        }
                    }
                    p.valList.removeIf(Objects::isNull);
                }
            });
            b.instList.removeIf(i -> (i.getDef() != null && varList.get(i.getDef()).p == C));
        });
        // remove defs
        for (Map.Entry<String, Var> v : varList.entrySet()) {
            Var va = v.getValue();
            String name = v.getKey();
            if (va.p == C) {
                for (IRInst i : va.use.keySet()) {
                    i.CP(name, va.value);
                }
            }
        } // replace uses
        func.blocks.forEach(b -> {
            IRInst l = b.instList.getLast();
            if (l instanceof Br br && br.cond instanceof Constant c) {
                b.instList.set(b.instList.size() - 1, new Jmp(c.value == 0 ? br.falseBlock : br.trueBlock));
                if (c.value == 0) {
                    br.trueBlock.removePred(b);
                } else {
                    br.falseBlock.removePred(b);
                }
            }
        });// simplify branches
    }

    public void reformat() {
        for (IRBlock b : func.blocks) {
            HashSet<Phi> ps = new HashSet<>();
            b.IRInsts.clear();
            for (IRInst i : b.instList) {
                if (i instanceof Phi p) {
                    ps.add(p);
                } else {
                    b.IRInsts.add(i);
                }
            }
            HashSet<String> t = new HashSet<>();
            for (Map.Entry<String, Phi> p : b.phis.entrySet()) {
                if (!ps.contains(p.getValue())) {
                    t.add(p.getKey());
                }
            }
            t.forEach(s -> b.phis.remove(s));
        }
    }

    public Var getVar(Entity e) {
        if (e instanceof Register r) {
            return varList.get(r.name);
        } else if (e instanceof Constant c) {
            return new Var(c.value);
        } else {
            return null;
        }
    }

    public void meet(Var dst, Var lhs, String op, Var rhs) {
        if (lhs.p == C && rhs.p == C && !(rhs.value == 0 && (op.equals("sdiv") || op.equals("srem")))) {
            long value = 0, lv = lhs.value, rv = rhs.value;
            switch (op) {
                case "add" -> value = lv + rv;
                case "sub" -> value = lv - rv;
                case "mul" -> value = lv * rv;
                case "sdiv" -> value = lv / rv;
                case "srem" -> value = lv % rv;
                case "shl" -> value = lv << rv;
                case "ashr" -> value = lv >> rv;
                case "and" -> value = lv & rv;
                case "or" -> value = lv | rv;
                case "xor" -> value = lv ^ rv;
                case "slt" -> value = (lv < rv) ? 1 : 0;
                case "sle" -> value = (lv <= rv) ? 1 : 0;
                case "sgt" -> value = (lv > rv) ? 1 : 0;
                case "sge" -> value = (lv >= rv) ? 1 : 0;
                case "eq" -> value = (lv == rv) ? 1 : 0;
                case "ne" -> value = (lv != rv) ? 1 : 0;
            }
            if (dst.p == U) {
                workList.add(dst);
            }
            dst.p = C;
            dst.value = value;
            return;
        }
        if (((lhs.p == C && lhs.value == 0) || (rhs.p == C && rhs.value == 0)) && op.equals("mul")) {
            if (dst.p == U) {
                workList.add(dst);
            }
            dst.p = C;
            dst.value = 0;
            return;
        }
        if (lhs.name != null && rhs.name != null && lhs.name.equals(rhs.name)) {
            if (op.equals("sub") || op.equals("xor") || op.equals("slt")
                    || op.equals("sgt") || op.equals("ne")) {
                if (dst.p == U) {
                    workList.add(dst);
                }
                dst.p = C;
                dst.value = 0;
                return;
            }
            if (op.equals("sle") || op.equals("sge") || op.equals("eq")) {
                if (dst.p == U) {
                    workList.add(dst);
                }
                dst.p = C;
                dst.value = 1;
                return;
            }
        }
        if (lhs.p == M || rhs.p == M) {
            if (dst.p != M) {
                workList.add(dst);
            }
            dst.p = M;
        }
    }

    public boolean cmp(long lhs, String op, long rhs) {
        boolean ret = false;
        switch (op) {
            case "slt" -> ret = lhs < rhs;
            case "sle" -> ret = lhs <= rhs;
            case "sgt" -> ret = lhs > rhs;
            case "sge" -> ret = lhs >= rhs;
            case "eq" -> ret = lhs == rhs;
            case "ne" -> ret = lhs != rhs;
        }
        return ret;
    }

    public void handlePhi(IRBlock b, Phi p) {
        Long uniqueConst = null;
        boolean isM = false;
        for (var e : p.valList) {
            if (queryPass(e.b.label, b.label) && !(e.a instanceof Register r && r.name.startsWith("@"))) {
                Var v = getVar(e.a);
                if (v.p == M) {
                    isM = true;
                    break;
                }
                if (v.p == C) {
                    if (uniqueConst == null) {
                        uniqueConst = v.value;
                    } else if (uniqueConst != v.value) {
                        isM = true;
                        break;
                    }
                }
            }
        }
        Var d = getVar(p.dest);
        if (isM) {
            setM(p.dest);
        } else if (uniqueConst != null) {
            setC(p.dest, uniqueConst);
        } else {
            d.p = U;
        }
    }

    public void handleBr(IRBlock b, Br br) {
        boolean isM = false;
        for (String s : br.getUse()) {
            Var v = varList.get(s);
            if (v.p == M) {
                isM = true;
            }
        }
        Blk tr = blockList.get(br.trueBlock.label);
        Blk fl = blockList.get(br.falseBlock.label);
        if (isM) {
            setExec(tr);
            setExec(fl);
            setPass(b.label, tr.blk.label);
            setPass(b.label, fl.blk.label);
        } else {
            if (br.cmp == null) {
                Var cond = getVar(br.cond);
                if (cond.p == C) {
                    if (cond.value == 0) {
                        setExec(fl);
                        setPass(b.label, fl.blk.label);
                    } else {
                        setExec(tr);
                        setPass(b.label, tr.blk.label);
                    }
                }
            } else {
                Var lhs = getVar(br.cmp.lhs);
                Var rhs = getVar(br.cmp.rhs);
                if (lhs.p == C && rhs.p == C) {
                    boolean res = cmp(lhs.value, br.cmp.op, rhs.value);
                    if (res) {
                        setExec(tr);
                        setPass(b.label, tr.blk.label);
                    } else {
                        setExec(fl);
                        setPass(b.label, fl.blk.label);
                    }
                } else if (lhs.p == M || rhs.p == M) {
                    setExec(tr);
                    setPass(b.label, tr.blk.label);
                    setExec(fl);
                    setPass(b.label, fl.blk.label);
                }
            }
        }
    }

    public void setM(Register r) {
        Var v = varList.get(r.name);
        if (v.p != M) {
            workList.add(v);
        }
        v.p = M;
    }

    public void setC(Register r, long val) {
        Var v = varList.get(r.name);
        if (v.p == M) {
            return;
        }
        if (v.p == U) {
            workList.add(v);
        }
        v.p = C;
        v.value = val;
    }

    public void setExec(Blk b) {
        if (!b.exec) {
            workList.add(b);
            for (Blk s : b.succ) {
                if (s.exec) {
                    workList.add(s);
                }
            }
        }
        b.exec = true;
    }

    public void setPass(String f, String t) {
        edges.get(t).get(f).pass = true;
    }

    public boolean queryPass(String f, String t) {
        return edges.get(t).get(f).pass;
    }

}
