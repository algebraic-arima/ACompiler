package src.IR.IRDef;

import org.antlr.v4.runtime.misc.Pair;
import src.IR.IRInst.*;
import src.IR.IRVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class IRBlock {
    public String label;
    public HashMap<String, Phi> phis = new HashMap<>();
    public ArrayList<IRInst> IRInsts = new ArrayList<>();
    public int index;
    static public int blockCnt;
    public int regNum = 0;
    public int funcParamMax = 0;
    public ArrayList<IRBlock> pred, succ;
    public ArrayList<IRInst> instList;
    public HashMap<String, ArrayList<Move>> mv;

    public IRBlock(String s) {
        index = blockCnt;
        ++blockCnt;
        if (!s.equals("entry")) {
            s = "l-" + index + "-" + s;
        }
        label = s;
        pred = new ArrayList<>();
        succ = new ArrayList<>();
        instList = new ArrayList<>();
        mv = new HashMap<>();
    }

    public void addInst(IRInst IRInst) {
        IRInsts.add(IRInst);
    }

    public void print() {
        if (!label.equals("entry")) {
            System.out.print(label + ":\n");
        }
        for (Phi p : phis.values()) {
            p.print();
        }
        for (IRInst i : IRInsts) {
            i.print();
        }
        System.out.print("\n");
    }

    public void reformat() {
        if (IRInsts.isEmpty()) return;
        ArrayList<IRInst> newIRInsts = new ArrayList<>();
        for (IRInst i : IRInsts) {
            newIRInsts.add(i);
            if (i instanceof Binary
                    || i instanceof Load || i instanceof GetElePtr
                    || i instanceof Icmp || i instanceof Phi) {
                regNum++;
            } else if (i instanceof Call c) {
                if (i.dest != null && !c.retType.typeName.equals("void")) {
                    regNum++;
                }
                if (c.args.size() > funcParamMax) {
                    funcParamMax = c.args.size();
                }
            } else if (i instanceof Alloca) {
                regNum += 2;
            }
            if (i instanceof terminalIRInst) {
                break;
            }
        }
        IRInsts = newIRInsts;
    }

    public void putPhi(String var, Phi p) {
        phis.put(var, p);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public HashSet<IRBlock> getSucc() {
        HashSet<IRBlock> succ = new HashSet<>();
        IRInst fin = IRInsts.getLast();
        if (fin instanceof Br br) {
            succ.add(br.trueBlock);
            succ.add(br.falseBlock);
        } else if (fin instanceof Jmp j) {
            succ.add(j.block);
        }
        return succ;
    }

    public void removePred(IRBlock pr) {
        for (var e : phis.entrySet()) {
            Phi p = e.getValue();
            for (int i = 0; i < p.valList.size(); ++i) {
                var d = p.valList.get(i);
                if (d.b == pr) {
                    p.valList.set(i, null);
                }
            }
            p.valList.removeIf(Objects::isNull);
        }
    }

    public void replacePred(IRBlock f, IRBlock t) {
        for (var e : phis.entrySet()) {
            Phi p = e.getValue();
            for (int i = 0; i < p.valList.size(); ++i) {
                var d = p.valList.get(i);
                if (d.b == f) {
                    p.valList.set(i, new Pair<>(d.a, t));
                }
            }
        }
    }
}
