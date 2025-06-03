package src.Optim.Inline;

import org.antlr.v4.runtime.misc.Pair;
import src.ASM.Operand.Reg;
import src.IR.IRBuilder;
import src.IR.IRDef.IRBlock;
import src.IR.IRDef.IRFuncDef;
import src.IR.IRInst.*;
import src.IR.IRProg;
import src.utils.Entity.Constant;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;

import java.util.*;

public class Inline {

    IRProg irProg;
    public HashMap<String, IRFuncDef> inlineList = new HashMap<>();
    public HashMap<IRBlock, ArrayList<IRBlock>> replaceList = new HashMap<>();
    static int inlineFuncNum = 0;
    static int inlineBlockNum = 0;

    public Inline(IRProg irProg) {
        this.irProg = irProg;
        for (IRFuncDef func : irProg.funcDefs) {
            if (!func.hasCall && !func.name.equals("@main")) {
                inlineList.put(func.name, func);
            }
        }
        if (!inlineList.isEmpty()) {
            inline();
            irProg.funcDefs.removeIf(d -> inlineList.containsKey(d.name));
        }
    }

    public void inline() {
        for (int i = 0; i < irProg.funcDefs.size(); ++i) {
            IRFuncDef f = irProg.funcDefs.get(i);
            if (!f.hasCall) continue;
            f.hasCall = false;
            for (int j = 0; j < f.blocks.size(); ++j) {
                IRBlock b = f.blocks.get(j);
                boolean inlineCall = false;
                for (int k = 0; k < b.IRInsts.size(); ++k) {
                    IRInst inst = b.IRInsts.get(k);
                    if (inst instanceof Call c) {
                        if (inlineList.containsKey(c.funcName)) {
                            inlineCall = true;
                        } else {
                            f.hasCall = true;
                        }
                    }
                }
                if (!inlineCall) {
                    continue;
                }
                HashSet<IRBlock> succBlock = b.getSucc();
                var l = inline(f, b);
                replaceList.put(b, l);
                IRBlock phiReplaceBlock = l.getLast();
                for (IRBlock s : succBlock) {
                    s.replacePred(b, phiReplaceBlock);
//                    for (Phi p : s.phis.values()) {
//                        for (int ii = 0; ii < p.valList.size(); ++ii) {
//                            var e = p.valList.get(ii);
//                            if (e.b == b) {
//                                p.valList.set(ii, new Pair<>(e.a, phiReplaceBlock));
//                            }
//                        }
//                    }
                }
            }
            ArrayList<IRBlock> newBlocks = new ArrayList<>();
            for (IRBlock b : f.blocks) {
                if (replaceList.containsKey(b)) {
                    newBlocks.addAll(replaceList.get(b));
                } else {
                    newBlocks.add(b);
                }
            }
            f.blocks = newBlocks;
            replaceList.clear();
        }
    }

    public ArrayList<IRBlock> inline(IRFuncDef func, IRBlock b) {
        ArrayList<IRBlock> retArray = new ArrayList<>();
        ArrayList<IRInst> tmpInsts = new ArrayList<>(b.IRInsts);
        IRBlock curBlock = b;
        b.IRInsts.clear();
        retArray.add(curBlock);
        for (IRInst i : tmpInsts) {
            if (i instanceof Call c && inlineList.containsKey(c.funcName)) {
                HashMap<String, IRBlock> blockList = new HashMap<>();
                String suf = "-inline-" + inlineFuncNum;
                ++inlineFuncNum;
                IRFuncDef f = inlineList.get(c.funcName);
                blockList.put("entry", curBlock);
                for (int bi = 1; bi < f.blocks.size(); ++bi) {
                    String origin = f.blocks.get(bi).label;
                    String newName = "inline-" + inlineBlockNum + "-" + origin;
                    ++inlineBlockNum;
                    IRBlock newBlock = new IRBlock(newName);
                    blockList.put(origin, newBlock);
                    retArray.add(newBlock);
                }
                String newName = "inline-" + inlineBlockNum;
                ++inlineBlockNum;
                IRBlock retBlock = new IRBlock(newName);
                retArray.add(retBlock);
                HashMap<Register, Entity> paramMap = new HashMap<>();
                for (int cnt = 0; cnt < c.args.size(); ++cnt) {
                    Entity e = c.args.get(cnt);
                    Register p = Register.newReg(c.argTypes.get(cnt), f.paramNames.get(cnt));
                    paramMap.put(p, e);
                }
                Phi p;
                if (c.dest == null) {
                    p = null;
                } else {
                    p = new Phi(c.dest, c.retType);
                }
                for (IRInst a : f.regCollector.IRInsts) {
                    if (a instanceof Alloca al) {
                        func.addAlloca((Alloca) al.rename(suf, paramMap));
                    } else {
                        assert false;
                    }
                }
                for (IRBlock ab : f.blocks) {
                    if (!ab.label.equals("entry")) {
                        curBlock = blockList.get(ab.label);
                    }
                    for (Phi phi : ab.phis.values()) {
                        Phi newPhi = (Phi) phi.rename(suf, paramMap);
                        for (int pcnt = 0; pcnt < newPhi.valList.size(); pcnt++) {
                            var e = newPhi.valList.get(pcnt);
                            newPhi.valList.set(pcnt, new Pair<>(e.a, blockList.get(e.b.label)));
                        }
                        curBlock.putPhi(newPhi.dest.name, newPhi);
                    }
                    for (IRInst ai : ab.IRInsts) {
                        if (ai instanceof terminalIRInst) {
                            if (ai instanceof Br br) {
                                IRInst re = br.rename(suf, paramMap, blockList.get(br.trueBlock.label), blockList.get(br.falseBlock.label));
                                curBlock.addInst(re);
                            } else if (ai instanceof Jmp jmp) {
                                curBlock.addInst(jmp.rename(blockList.get(jmp.block.label)));
                            } else if (ai instanceof Ret ret) {
                                curBlock.addInst(ret.rename(retBlock));
                                if (p != null && ret.value != null) {
                                    Entity newValue;
                                    if (ret.value instanceof Register r) {
                                        if (paramMap.containsKey(r)) {
                                            newValue = paramMap.get(r);
                                        } else if (r.name.startsWith("@")) {
                                            newValue = r;
                                        } else {
                                            newValue = Register.newReg(r.type, r.name + suf);
                                        }
                                    } else {
                                        newValue = ret.value;
                                    }
                                    p.valList.add(new Pair<>(newValue, curBlock));
                                }
                            }
                        } else if (ai instanceof Phi phi) {
                            Phi newPhi = (Phi) phi.rename(suf, paramMap);
                            for (int pcnt = 0; pcnt < newPhi.valList.size(); pcnt++) {
                                var e = newPhi.valList.get(pcnt);
                                newPhi.valList.set(pcnt, new Pair<>(e.a, blockList.get(e.b.label)));
                            }
                            curBlock.putPhi(newPhi.dest.name, newPhi);
                        } else {
                            curBlock.addInst(ai.rename(suf, paramMap));
                        }
                    }
                }
                curBlock = retBlock;
                if (p != null) {
                    curBlock.putPhi(p.dest.name, p);
                }
                blockList.clear();
            } else {
                curBlock.addInst(i);
            }
        }
        return retArray;
    }


}
