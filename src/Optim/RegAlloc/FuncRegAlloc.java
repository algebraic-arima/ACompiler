package src.Optim.RegAlloc;

import org.antlr.v4.runtime.misc.Pair;
import src.IR.IRDef.IRBlock;
import src.IR.IRDef.IRFuncDef;
import src.IR.IRInst.*;
import src.utils.Entity.*;

import java.util.*;

public class FuncRegAlloc {

    static int loopConst = 16;
    public IRFuncDef irFunc;
    public HashMap<String, HashSet<Pair<IRBlock, IRInst>>> varUseList;
    public HashMap<String, Pair<IRBlock, IRInst>> varDefInst;
    public HashMap<IRInst, IRInst> preInst;
    HashSet<IRBlock> visBlocks;
    public HashMap<String, Double> cost;
    double loop = 1;
    public RIG rig;
    HashMap<String, Integer> spillList;

    public FuncRegAlloc(IRFuncDef irFunc) {
        this.irFunc = irFunc;
        varUseList = new HashMap<>();
        preInst = new HashMap<>();
        cost = new HashMap<>();
        varDefInst = new HashMap<>();

        varDefCollect();
        varUseCollect();

        rig = new RIG();
        rig.add(cost);
        liveAnalysis();

        int regNum = 12;
        spill(regNum);
        rig.MCS(false);
        rig.MCS(true);
        rig.color(regNum);
        rig.colorStack();
        reducePhi();
        // have no data hazard in phi
    }

    public void varDefCollect() {
        // collect var defs
        for (IRBlock b : irFunc.blocks) {
            if (b.label.endsWith("-for-cond")
                    || b.label.endsWith("-while-cond")
                    || b.label.endsWith("-na-cond")) {
                loop *= loopConst;
            } else if (b.label.endsWith("-for-end")
                    || b.label.endsWith("-while-end")
                    || b.label.endsWith("-na-end")) {
                loop /= loopConst;
            }
            IRInst te = b.IRInsts.getLast();
            if (te instanceof Br br) {
                b.succ.add(br.trueBlock);
                b.succ.add(br.falseBlock);
                br.trueBlock.pred.add(b);
                br.falseBlock.pred.add(b);
            } else if (te instanceof Jmp j) {
                b.succ.add(j.block);
                j.block.pred.add(b);
            } else {
                b.succ.clear();
            }
            for (IRInst i : b.instList) {
                String def = i.getDef();
                if (def != null) {
                    varUseList.put(def, new HashSet<>());
                    varDefInst.put(def, new Pair<>(b, i));
                    if (!cost.containsKey(def)) {
                        cost.put(def, loop);
                    } else {
                        cost.put(def, cost.get(def) + loop);
                    }
                }
            }
        }
    }

    public void varUseCollect() {
        // collect uses
        loop = 1;
        for (int bi = 0; bi < irFunc.blocks.size(); ++bi) {
            IRBlock b = irFunc.blocks.get(bi);
            IRInst cur = null;
            if (b.label.endsWith("-for-cond")
                    || b.label.endsWith("-while-cond")
                    || b.label.endsWith("-na-cond")) {
                loop *= loopConst;
            } else if (b.label.endsWith("-for-end")
                    || b.label.endsWith("-while-end")
                    || b.label.endsWith("-na-end")) {
                loop /= loopConst;
            }
            for (IRInst i : b.instList) {
                preInst.put(i, cur);
                cur = i;
                HashSet<String> use = i.getUse();
                for (String s : use) {
                    if (varUseList.containsKey(s)) {
                        varUseList.get(s).add(new Pair<>(b, i));
                        if (!cost.containsKey(s)) {
                            cost.put(s, loop);
                        } else {
                            cost.put(s, cost.get(s) + loop);
                        }
                    }
                }
            }
        }
    }

    public void liveAnalysis() {
        visBlocks = new HashSet<>();
        for (Map.Entry<String, HashSet<Pair<IRBlock, IRInst>>> e : varUseList.entrySet()) {
            visBlocks.clear();
            String v = e.getKey();
            HashSet<Pair<IRBlock, IRInst>> uses = e.getValue();
            for (var use : uses) {
                IRBlock useBlock = use.a;
                IRInst useInst = use.b;
                if (useInst instanceof Phi p) {
                    for (int i = 0; i < p.valList.size(); ++i) {
                        var va = p.valList.get(i);
                        Entity en = va.a;
                        if (en instanceof Register r) {
                            if (r.name.equals(v)) {
                                if (!visBlocks.contains(va.b)) {
                                    visBlocks.add(va.b);
                                    liveOut(va.b, va.b.instList.getLast(), v);
                                }
                            }
                        }
                    }
                } else {
                    IRInst pre = preInst.get(useInst);
                    if (pre == null) {
                        for (IRBlock p : useBlock.pred) {
                            if (!visBlocks.contains(p)) {
                                visBlocks.add(p);
                                liveOut(p, p.instList.getLast(), v);
                            }
                        }
                    } else {
                        liveOut(useBlock, pre, v);
                    }
                }
            }
        }
    }

    public void liveOut(IRBlock b, IRInst i, String v) {
        i.addOut(v);
        HashSet<String> def = new HashSet<>();
        String d = i.getDef();
        if (d != null) {
            def.add(d);
        }
        for (String y : def) {
            if (y.equals(v)) continue;
            if (!cost.containsKey(y)) {
                continue;
            }
            rig.addEdge(v, y);
            // y: def in the inst
            // v: some active var suring the inst
        }
        if (!def.contains(v)) {
            IRInst pre = preInst.get(i);
            if (pre == null) {
                for (IRBlock p : b.pred) {
                    if (!visBlocks.contains(p)) {
                        visBlocks.add(p);
                        liveOut(p, p.instList.getLast(), v);
                    }
                }
            } else {
                liveOut(b, pre, v);
            }
        }
    }

    public void spill(int rn) {
        HashMap<String, Double> tmp = new HashMap<>();
        for (var m : cost.entrySet()) {
            tmp.put(m.getKey(), m.getValue() / (1 + rig.getN(m.getKey())));
        }
        cost = tmp;
        spillList = new HashMap<>();
        for (IRBlock b : irFunc.blocks) {
            for (IRInst i : b.instList) {
                int cnt = 0;
                for (var r : i.liveOut) {
                    String s = r.name;
                    if (!spillList.containsKey(s)) {
                        ++cnt;
                    }
                }
                if (cnt > rn) {
                    // select the min-cost (cnt - rn) vars to spill
                    PriorityQueue<Pair<String, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.b));
                    for (Register r : i.liveOut) {
                        String s = r.name;
                        if (!spillList.containsKey(s)) {
                            pq.offer(new Pair<>(s, cost.get(s).intValue()));
                        }
                    }
                    for (int j = 0; j < cnt - rn; ++j) {
                        Pair<String, Integer> p = pq.poll();
                        spillList.put(p.a, p.b);
                    }
                    i.omega = rn;
                } else {
                    i.omega = cnt;
                }
            }
        }
        for (Map.Entry<String, Integer> e : spillList.entrySet()) {
//            System.out.println("  " + e.getKey() + " " + e.getValue());
            Register.markStack(e.getKey(), 1);
            rig.spillVertex(e.getKey());
        }
        rig.subgraph();
        irFunc.stackSize = spillList.size();
    }

    public void reducePhi() {
        for (IRBlock b : irFunc.blocks) {
            HashMap<Register, Phi> phis = new HashMap<>();
            HashMap<String, ArrayList<Pair<Pair<Entity, Register>, Boolean>>> totMVList = new HashMap<>();
            for (IRBlock p : b.pred) {
                totMVList.put(p.label, new ArrayList<>());
            }
            for (IRInst i : b.instList) {
                if (i instanceof Phi p) {
                    phis.put(p.dest, p);
                    for (var c : p.valList) {
                        boolean coalesce = false;
                        if (c.a instanceof Register r && p.dest.color < 0) {
                            if (!p.liveOut.contains(r) && r.color < 0) {
                                coalesce = true;
                            }
                        }
                        totMVList.get(c.b.label).add(new Pair<>(new Pair<>(c.a, p.dest), coalesce));
                    }
                } else break;
            }
            // no need to consider parallel data shifting
            // since you don't know where it will be assigned
            for (IRBlock p : b.pred) {
                // apply modifies to p.mv
                ArrayList<Pair<Pair<Entity, Register>, Boolean>> mvList = totMVList.get(p.label);
                ArrayList<Move> moveArray = new ArrayList<>();
                p.mv.put(b.label, moveArray);
                for (var e : mvList) {
                    Move m = new Move(e.a.a, e.a.b);
                    moveArray.add(m);
                }
            }
            // reserve those not trimmed, using phis
        }
    }
}
