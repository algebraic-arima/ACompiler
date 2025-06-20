package src.ASM;

import src.ASM.ASMDef.*;
import src.ASM.ASMInst.*;
import src.ASM.ASMInst.BIN.*;
import src.ASM.ASMInst.BR.*;
import src.ASM.ASMInst.MV;
import src.ASM.Operand.*;

import src.IR.IRDec.IRFuncDec;
import src.IR.IRDec.IRGVarDec;
import src.IR.IRDef.*;
import src.IR.IRInst.*;
import src.IR.IRProg;
import src.IR.IRVisitor;
import src.utils.Entity.Constant;
import src.utils.Entity.Entity;
import src.utils.Entity.Register;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class ASMBuilder implements IRVisitor {

    public ASMProg asmProg;
    public ASMFuncDef curFunc;
    public ASMBlock curBlock;
    public HashMap<String, Integer> classDefs; // number of fields in each class
    public HashMap<String, Integer> regPos;
    public IRFuncDef curIRFuncDef;
    public IRBlock curIRBlock;
    public int occSP = 0;
    boolean p;

    public ASMBuilder(IRProg prog, boolean p) {
        this.p = p;
        classDefs = new HashMap<>();
        curIRFuncDef = null;
        curIRBlock = null;
        prog.classDefs.forEach(x -> classDefs.put(x.className, x.fields.size()));
        this.asmProg = new ASMProg();
        regPos = new HashMap<>();
        visit(prog);
    }

    @Override
    public void visit(IRProg node) {
        node.gVarDefs.forEach(x -> x.accept(this));
        node.strDef.accept(this);
        node.funcDefs.forEach(x -> x.accept(this));
        if (p) {
            for (Map.Entry<String, Integer> entry : regPos.entrySet()) {
                System.out.println("; " + entry.getKey() + " " + entry.getValue());
            }
        }
    }

    @Override
    public void visit(IRClassDef node) {

    }

    @Override
    public void visit(IRFuncDef node) {
        curFunc = new ASMFuncDef(node.name.substring(1));
        curIRFuncDef = node;
        curFunc.fd = node.fd;
        asmProg.funcDefs.add(curFunc);

        ASMBlock in = new ASMBlock(node.name.substring(1));
        curFunc.blocks.add(in);
        curBlock = in;
        int stackSize = 4 * (node.paramNames.size() + node.regNum + node.stackSize + (node.funcParamMax > 8 ? node.funcParamMax - 8 : 0) + 1);
        curFunc.stackSize = (stackSize / 16 + 1) * 16;
        if (curFunc.stackSize <= 2047) {
            addADDI("sp", "sp", -curFunc.stackSize);
        } else {
            curBlock.addInst(new LI("t0", -curFunc.stackSize));
            curBlock.addInst(new ADD("sp", "sp", "t0"));
        }
        occSP = 4;
        addSW("ra", curFunc.stackSize - occSP, "sp"); // caller saved

        for (int i = 0; i < node.regNum; ++i) {
            occSP += 4;
            addSW("s" + i, curFunc.stackSize - occSP, "sp");
        }
        for (int i = 0; i < node.paramNames.size() && i < 8; ++i) {
            occSP += 4;
            addSW("a" + i, curFunc.stackSize - occSP, "sp");
            regPos.put(node.paramNames.get(i), curFunc.stackSize - occSP);
        }
        node.blocks.forEach(x -> x.accept(this));
        curIRFuncDef = null;

    }

    @Override
    public void visit(IRGlobalVarDef node) {
        asmProg.gVarDefs.add(new ASMGVarDef(node.name.substring(1), node.value));
    }

    @Override
    public void visit(IRStrDef node) {
        node.strMap.forEach((k, v) -> {
            asmProg.gStrDefs.add(new ASMGStrDef("constStr_" + v, k));
        });
    }

    @Override
    public void visit(IRFuncDec node) {

    }

    @Override
    public void visit(IRGVarDec node) {

    }

    @Override
    public void visit(IRBlock node) {
        curIRBlock = node;
        if (node.IRInsts.isEmpty() && node.phis.isEmpty()) return;
        String bn = node.label;
        if (node.label.equals("entry")) {
            bn = curFunc.name;
        }
        if (!curBlock.label.equals(bn)) {
            curBlock = new ASMBlock(bn.replace("-", "_"));
            curFunc.blocks.add(curBlock);
        }
        node.IRInsts.forEach(x -> x.accept(this));
        curIRBlock = null;
    }

    String fetchReg(Register r, String tmp) {
        if (r == null) return "zero";
        if (r.color > 0) {
            return Reg.freeRegs[r.color];
        } else if (r.color < 0) {
            addLW(tmp, regPos.get(r.name), "sp");
            return tmp;
        } else {
            if (r.name.endsWith(".val") || r.name.equals("%this1")) {
                int ind = curIRFuncDef.paramNames.indexOf(r.name);
                if (ind < 8) {
                    addLW(tmp, regPos.get(r.name), "sp");
                    return tmp;
                } else {
                    addLW(tmp, curFunc.stackSize + (ind - 8) * 4L, "sp");
                    return tmp;
                }
            }
        }
        return null;
    }

    String storeReg(Register r, String src) {
        if (r.color < 0) {
            occSP += 4;
            regPos.put(r.name, curFunc.stackSize - occSP);
            addSW(src, curFunc.stackSize - occSP, "sp");
            return src;
        } else if (r.color > 0) {
            return Reg.freeRegs[r.color];
        }
        return null;
    }

    @Override
    public void visit(Alloca node) {
        if (node.dest.color < 0) {
            occSP += 8;
            regPos.put(node.dest.name, curFunc.stackSize - occSP);
            addADDI("t0", "sp", curFunc.stackSize - occSP + 4);
            addSW("t0", regPos.get(node.dest.name), "sp");
        } else {
            occSP += 4;
            addADDI(Reg.freeRegs[node.dest.color], "sp", curFunc.stackSize - occSP);
        }
    }

    @Override
    public void visit(Binary node) {
        if (node.op.equals("add")) {
            if (node.lhs instanceof Register l) {
                String l_name = fetchReg(l, "t1");
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        curBlock.addInst(new ADD("t0", l_name, r_name));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new ADD(storeReg(node.dest, null), l_name, r_name));
                    }
                } else if (node.rhs instanceof Constant c) {
                    if (node.dest.color < 0) {
                        addADDI("t0", l_name, c.value);
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        addADDI(storeReg(node.dest, null), l_name, c.value);
                    }
                }
            } else if (node.lhs instanceof Constant c) {
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        addADDI("t0", r_name, c.value);
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        addADDI(storeReg(node.dest, null), r_name, c.value);
                    }
                } else if (node.rhs instanceof Constant cr) {
                    curBlock.addInst(new LI("t0", c.value));
                    curBlock.addInst(new LI("t1", cr.value));
                    if (node.dest.color < 0) {
                        curBlock.addInst(new ADD("t0", "t0", "t1"));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new ADD(storeReg(node.dest, null), "t0", "t1"));
                    }
                }
            }
        } else if (node.op.equals("sub")) {
            if (node.lhs instanceof Register l) {
                String l_name = fetchReg(l, "t1");
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SUB("t0", l_name, r_name));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SUB(storeReg(node.dest, null), l_name, r_name));
                    }
                } else if (node.rhs instanceof Constant c) {
                    if (node.dest.color < 0) {
                        addADDI("t0", l_name, -c.value);
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        addADDI(storeReg(node.dest, null), l_name, -c.value);
                    }
                }
            } else if (node.lhs instanceof Constant c) {
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        addADDI("t0", r_name, -c.value);
                        curBlock.addInst(new SUB("t0", "zero", "t0"));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        String ss = storeReg(node.dest, null);
                        addADDI(ss, r_name, -c.value);
                        curBlock.addInst(new SUB(ss, "zero", ss));
                    }
                } else if (node.rhs instanceof Constant cr) {
                    curBlock.addInst(new LI("t0", c.value));
                    curBlock.addInst(new LI("t1", cr.value));
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SUB("t0", "t0", "t1"));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SUB(storeReg(node.dest, null), "t0", "t1"));
                    }
                }
            }
        } else if (node.op.equals("mul") || node.op.equals("sdiv") || node.op.equals("srem")) {
            String l_name = null, r_name = null;
            if (node.op.equals("mul")) {
                if (node.lhs instanceof Constant c && c.log2() != -1) {
                    if (node.rhs instanceof Constant rc) {
                        if (node.dest.color < 0) {
                            curBlock.addInst(new LI("t0", c.value * rc.value));
                            storeReg(node.dest, "t0");
                        } else if (node.dest.color > 0) {
                            curBlock.addInst(new LI(storeReg(node.dest, null), c.value * rc.value));
                        }
                    } else if (node.rhs instanceof Register r) {
                        int o = c.log2();
                        r_name = fetchReg(r, "t1");
                        if (node.dest.color < 0) {
                            curBlock.addInst(new SLLI("t0", r_name, o));
                            storeReg(node.dest, "t0");
                        } else if (node.dest.color > 0) {
                            curBlock.addInst(new SLLI(storeReg(node.dest, null), r_name, o));
                        }
                    }
                    return;
                }
                if (node.rhs instanceof Constant c && c.log2() != -1) {
                    if (node.lhs instanceof Constant lc) {
                        if (node.dest.color < 0) {
                            curBlock.addInst(new LI("t0", c.value * lc.value));
                            storeReg(node.dest, "t0");
                        } else if (node.dest.color > 0) {
                            curBlock.addInst(new LI(storeReg(node.dest, null), c.value * lc.value));
                        }
                    } else if (node.lhs instanceof Register r) {
                        int o = c.log2();
                        l_name = fetchReg(r, "t1");
                        if (node.dest.color < 0) {
                            curBlock.addInst(new SLLI("t0", l_name, o));
                            storeReg(node.dest, "t0");
                        } else if (node.dest.color > 0) {
                            curBlock.addInst(new SLLI(storeReg(node.dest, null), l_name, o));
                        }
                    }
                    return;
                }
            }
            if (node.lhs instanceof Constant) {
                curBlock.addInst(new LI("t0", ((Constant) node.lhs).value));
                l_name = "t0";
            } else if (node.lhs instanceof Register r) {
                l_name = fetchReg(r, "t0");
            }
            if (node.rhs instanceof Constant) {
                curBlock.addInst(new LI("t1", ((Constant) node.rhs).value));
                r_name = "t1";
            } else if (node.rhs instanceof Register r) {
                r_name = fetchReg(r, "t1");
            }
            if (node.op.equals("mul")) {
                if (node.dest.color < 0) {
                    curBlock.addInst(new MUL("t0", l_name, r_name));
                    storeReg(node.dest, "t0");
                } else if (node.dest.color > 0) {
                    curBlock.addInst(new MUL(storeReg(node.dest, null), l_name, r_name));
                }
            } else if (node.op.equals("sdiv")) {
                if (node.dest.color < 0) {
                    curBlock.addInst(new DIV("t0", l_name, r_name));
                    storeReg(node.dest, "t0");
                } else if (node.dest.color > 0) {
                    curBlock.addInst(new DIV(storeReg(node.dest, null), l_name, r_name));
                }
            } else {
                if (node.dest.color < 0) {
                    curBlock.addInst(new REM("t0", l_name, r_name));
                    storeReg(node.dest, "t0");
                } else if (node.dest.color > 0) {
                    curBlock.addInst(new REM(storeReg(node.dest, null), l_name, r_name));
                }
            }
        } else if (node.op.equals("shl")) {
            if (node.lhs instanceof Register l) {
                String l_name = fetchReg(l, "t1");
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SLL("t0", l_name, r_name));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SLL(storeReg(node.dest, null), l_name, r_name));
                    }
                } else if (node.rhs instanceof Constant c) {
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SLLI("t0", l_name, c.value));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SLLI(storeReg(node.dest, null), l_name, c.value));
                    }
                }
            } else if (node.lhs instanceof Constant c) {
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    curBlock.addInst(new LI("t0", c.value));
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SLL("t0", "t0", r_name));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SLL(storeReg(node.dest, null), "t0", r_name));
                    }
                } else if (node.rhs instanceof Constant cr) {
                    curBlock.addInst(new LI("t0", c.value));
                    curBlock.addInst(new LI("t1", cr.value));
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SLL("t0", "t0", "t1"));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SLL(storeReg(node.dest, null), "t0", "t1"));
                    }
                }
            }
        } else if (node.op.equals("ashr")) {
            if (node.lhs instanceof Register l) {
                String l_name = fetchReg(l, "t1");
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SRA("t0", l_name, r_name));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SRA(storeReg(node.dest, null), l_name, r_name));
                    }
                } else if (node.rhs instanceof Constant c) {
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SRAI("t0", l_name, c.value));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SRAI(storeReg(node.dest, null), l_name, c.value));
                    }
                }
            } else if (node.lhs instanceof Constant c) {
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    curBlock.addInst(new LI("t0", c.value));
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SRA("t0", r_name, "t0"));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SRA(storeReg(node.dest, null), r_name, "t0"));
                    }
                } else if (node.rhs instanceof Constant cr) {
                    curBlock.addInst(new LI("t0", c.value));
                    curBlock.addInst(new LI("t1", cr.value));
                    if (node.dest.color < 0) {
                        curBlock.addInst(new SRA("t0", "t0", "t1"));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new SRA(storeReg(node.dest, null), "t0", "t1"));
                    }
                }
            }
        } else if (node.op.equals("and")) {
            if (node.lhs instanceof Register l) {
                String l_name = fetchReg(l, "t1");
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        curBlock.addInst(new AND("t0", l_name, r_name));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new AND(storeReg(node.dest, null), l_name, r_name));
                    }
                } else if (node.rhs instanceof Constant c) {
                    if (node.dest.color < 0) {
                        addANDI("t0", l_name, c.value);
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        addANDI(storeReg(node.dest, null), l_name, c.value);
                    }
                }
            } else if (node.lhs instanceof Constant c) {
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        addANDI("t0", r_name, c.value);
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        addANDI(storeReg(node.dest, null), r_name, c.value);
                    }
                } else if (node.rhs instanceof Constant cr) {
                    curBlock.addInst(new LI("t0", c.value));
                    curBlock.addInst(new LI("t1", cr.value));
                    if (node.dest.color < 0) {
                        curBlock.addInst(new AND("t0", "t0", "t1"));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new AND(storeReg(node.dest, null), "t0", "t1"));
                    }
                }
            }
        } else if (node.op.equals("or")) {
            if (node.lhs instanceof Register l) {
                String l_name = fetchReg(l, "t1");
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        curBlock.addInst(new OR("t0", l_name, r_name));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new OR(storeReg(node.dest, null), l_name, r_name));
                    }
                } else if (node.rhs instanceof Constant c) {
                    if (node.dest.color < 0) {
                        addORI("t0", l_name, c.value);
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        addORI(storeReg(node.dest, null), l_name, c.value);
                    }
                }
            } else if (node.lhs instanceof Constant c) {
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        addORI("t0", r_name, c.value);
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        addORI(storeReg(node.dest, null), r_name, c.value);
                    }
                } else if (node.rhs instanceof Constant cr) {
                    curBlock.addInst(new LI("t0", c.value));
                    curBlock.addInst(new LI("t1", cr.value));
                    if (node.dest.color < 0) {
                        curBlock.addInst(new OR("t0", "t0", "t1"));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new OR(storeReg(node.dest, null), "t0", "t1"));
                    }
                }
            }
        } else if (node.op.equals("xor")) {
            if (node.lhs instanceof Register l) {
                String l_name = fetchReg(l, "t1");
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        curBlock.addInst(new XOR("t0", l_name, r_name));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new XOR(storeReg(node.dest, null), l_name, r_name));
                    }
                } else if (node.rhs instanceof Constant c) {
                    if (node.dest.color < 0) {
                        addXORI("t0", l_name, c.value);
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        addXORI(storeReg(node.dest, null), l_name, c.value);
                    }
                }
            } else if (node.lhs instanceof Constant c) {
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    if (node.dest.color < 0) {
                        addXORI("t0", r_name, c.value);
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        addXORI(storeReg(node.dest, null), r_name, c.value);
                    }
                } else if (node.rhs instanceof Constant cr) {
                    curBlock.addInst(new LI("t0", c.value));
                    curBlock.addInst(new LI("t1", cr.value));
                    if (node.dest.color < 0) {
                        curBlock.addInst(new XOR("t0", "t0", "t1"));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new XOR(storeReg(node.dest, null), "t0", "t1"));
                    }
                }
            }
        }
    }


    @Override
    public void visit(Br node) {
        if (node.cmp == null) {
            if (node.cond instanceof Constant c) {
                if (c.value == 0) {
                    curBlock.addInst(new J(node.falseBlock.label));
                } else {
                    curBlock.addInst(new J(node.trueBlock.label));
                }
            } else if (node.cond instanceof Register reg) {
                String nlabel = curBlock.label + "_forb";
                curBlock.addInst(new BEQZ(fetchReg(reg, "t0"), nlabel));
                var mv1 = curIRBlock.mv.get(node.trueBlock.label);
                addMVList(mv1);
                curBlock.addInst(new J(node.trueBlock.label));
                curBlock = new ASMBlock(nlabel);
                var mv2 = curIRBlock.mv.get(node.falseBlock.label);
                addMVList(mv2);
                curBlock.addInst(new J(node.falseBlock.label));
                curFunc.blocks.add(curBlock);
            }
        } else {
            String ln = null, rn = null;
            if (node.cmp.lhs instanceof Constant c) {
                curBlock.addInst(new LI("t1", c.value));
                ln = "t1";
            } else if (node.cmp.lhs instanceof Register r) {
                ln = fetchReg(r, "t1");
            }
            if (node.cmp.rhs instanceof Constant c) {
                curBlock.addInst(new LI("t2", c.value));
                rn = "t2";
            } else if (node.cmp.rhs instanceof Register r) {
                rn = fetchReg(r, "t2");
            }
            String nlabel = curBlock.label + "_true"; // truelabel
            if (node.cmp.op.equals("eq")) {
                curBlock.addInst(new BEQ(ln, rn, nlabel));
            } else if (node.cmp.op.equals("ne")) {
                curBlock.addInst(new BNE(ln, rn, nlabel));
            } else if (node.cmp.op.equals("slt")) {
                curBlock.addInst(new BLT(ln, rn, nlabel));
            } else if (node.cmp.op.equals("sgt")) {
                curBlock.addInst(new BGT(ln, rn, nlabel));
            } else if (node.cmp.op.equals("sle")) {
                curBlock.addInst(new BLE(ln, rn, nlabel));
            } else if (node.cmp.op.equals("sge")) {
                curBlock.addInst(new BGE(ln, rn, nlabel));
            }
            var mv1 = curIRBlock.mv.get(node.falseBlock.label);
            addMVList(mv1);
            curBlock.addInst(new J(node.falseBlock.label));
            curBlock = new ASMBlock(nlabel);
            var mv2 = curIRBlock.mv.get(node.trueBlock.label);
            addMVList(mv2);
            curBlock.addInst(new J(node.trueBlock.label));
            curFunc.blocks.add(curBlock);
        }
    }

    @Override
    public void visit(Call node) {
        int funcParamNum = curIRFuncDef.paramNames.size();
        int cnt = 0;
//        for (String n : curIRFuncDef.paramNames) {
//            if (cnt < 8) {
//                occSP += 4;
//                curBlock.addInst(new SW("a" + cnt,
//                        curFunc.stackSize - occSP, "sp"));
//            } else break;
//            ++cnt;
//        }
//        cnt = 0;
        for (Entity e : node.args) {
            if (cnt < 8) {
                if (e instanceof Constant) {
                    curBlock.addInst(new LI("a" + cnt, ((Constant) e).value));
                } else if (e instanceof Register r) {
                    if (r.name.startsWith("%")) { // some variable
                        if (r.color < 0) {
                            addLW("a" + cnt, regPos.get(r.name), "sp");
                        } else if (r.color > 0) {
                            curBlock.addInst(new MV("a" + cnt, Reg.freeRegs[r.color]));
                        } else if (r.name.endsWith(".val") || r.name.equals("%this1")) {
                            int i = curIRFuncDef.paramNames.indexOf(r.name);
                            if (i < 8) {
                                curBlock.addInst(new LW("a" + cnt, regPos.get(r.name), "sp"));
//                                if (i >= cnt) {
//                                    // fetch from "a" reg
//                                    if (i != cnt) curBlock.addInst(new MV("a" + cnt, "a" + i));
//                                } else {
//                                    // fetch from stack
//                                    curBlock.addInst(new LW("a" + cnt, curFunc.stackSize - occSP + 4L * (funcParamNum - i - 1), "sp"));
//                                }
                            } else {
                                curBlock.addInst(new LW("a" + cnt, curFunc.stackSize + (i - 8) * 4L, "sp"));
                            }
                        }
                    } else {
                        curBlock.addInst(new LA("a" + cnt, ((Register) e).name.substring(1).replace("-", "_")));
                    }
                }
            } else {
                if (e instanceof Constant) {
                    curBlock.addInst(new LI("t0", ((Constant) e).value));
                    addSW("t0", (cnt - 8) * 4L, "sp");
                } else if (e instanceof Register r) {
                    String n = null;
                    if (r.name.startsWith("%")) {
                        n = fetchReg(r, "t0");
                    } else {
                        curBlock.addInst(new LA("t0", r.name.substring(1).replace("-", "_")));
                        n = "t0";
                    }
                    addSW(n, (cnt - 8) * 4L, "sp");
                }
            }
            ++cnt;
        }
        curBlock.addInst(new CALL(node.funcName.substring(1)));
        if (node.dest != null && !node.retType.typeName.equals("void")) {
            if (node.dest.color < 0) {
                occSP += 4;
                regPos.put(node.dest.name, curFunc.stackSize - occSP);
                addSW("a0", curFunc.stackSize - occSP, "sp");
            } else if (node.dest.color > 0) {
                curBlock.addInst(new MV(storeReg(node.dest, null), "a0"));
            }
        }
//        cnt = curIRFuncDef.paramNames.size();
//        if (cnt > 8) cnt = 8;
//        while (cnt-- > 0) {
//            curBlock.addInst(new LW("a" + cnt, curFunc.stackSize - occSP, "sp"));
//            occSP -= 4;
//        }
    }

    @Override
    public void visit(GetElePtr node) {
        String ptrName;
        if (node.ptr != null) {
            ptrName = fetchReg(node.ptr, "t0");
        } else {
            ptrName = "zero";
        }
        Integer csize = classDefs.get(node.ptrType.typeName);
        if (csize == null) {
            if (node.ptrType.typeName.equals("i32") || node.ptrType.typeName.equals("ptr"))
                csize = 1;
            else if (node.ptrType.typeName.equals("i1")) {
                csize = null;
            }
        }
        if (node.offset instanceof Constant c) {
            if (node.dest.color < 0) {
                addADDI("t0", ptrName, c.value * csize * 4);
                if (node.fieldInd > 0) {
                    addADDI("t0", "t0", node.fieldInd * 4L);
                }
                storeReg(node.dest, "t0");
            } else if (node.dest.color > 0) {
                String dest_reg = storeReg(node.dest, null);
                addADDI(dest_reg, ptrName, c.value * csize * 4);
                if (node.fieldInd > 0) {
                    addADDI(dest_reg, dest_reg, node.fieldInd * 4L);
                }
            }
        } else if (node.offset instanceof Register r) {
            String offset_name = fetchReg(r, "t1");
            if (node.dest.color < 0) {
                if (csize != null) {
                    curBlock.addInst(new SLLI("t1", offset_name, 2));
                    curBlock.addInst(new ADD("t0", ptrName, "t1"));
                } else {
                    curBlock.addInst(new ADD("t0", ptrName, offset_name));
                }
                if (node.fieldInd > 0) {
                    addADDI("t0", "t0", node.fieldInd * 4L);
                }
                storeReg(node.dest, "t0");
            } else if (node.dest.color > 0) {
                String dest_reg = storeReg(node.dest, null);
                if (csize != null) {
                    curBlock.addInst(new SLLI("t1", offset_name, 2));
                    curBlock.addInst(new ADD(dest_reg, ptrName, "t1"));
                } else {
                    curBlock.addInst(new ADD(dest_reg, ptrName, offset_name));
                }
                if (node.fieldInd > 0) {
                    addADDI(dest_reg, dest_reg, node.fieldInd * 4L);
                }
            }
        }
    }

    @Override
    public void visit(Icmp node) {
//        if (node.rhs == null || node.lhs == null) {
//            String rn;
//            if (node.lhs != null) rn = ((Register) node.lhs).name;
//            else if (node.rhs != null) rn = ((Register) node.rhs).name;
//            else return;
//            addLW("t0", regPos.get(rn), "sp");
//            if (node.op.equals("eq")) {
//                curBlock.addInst(new SEQZ("t0", "t0"));
//            } else {
//                curBlock.addInst(new SNEZ("t0", "t0"));
//            }
//            String rdn = node.dest.name;
//            occSP += 4;
//            regPos.put(rdn, curFunc.stackSize - occSP);
//            addSW("t0", curFunc.stackSize - occSP, "sp");
//            return;
//        }
        if (node.op.equals("eq")) {
            if (node.lhs instanceof Register l) {
                String l_name = fetchReg(l, "t1");
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    curBlock.addInst(new XOR("t0", l_name, r_name));
                    if (node.dest.color > 0) {
                        curBlock.addInst(new SEQZ(Reg.freeRegs[node.dest.color], "t0"));
                    } else if (node.dest.color < 0) {
                        curBlock.addInst(new SEQZ("t0", "t0"));
                        storeReg(node.dest, "t0");
                    }
                } else {
                    addXORI("t0", l_name, ((Constant) node.rhs).value);
                    if (node.dest.color > 0) {
                        curBlock.addInst(new SEQZ(Reg.freeRegs[node.dest.color], "t0"));
                    } else if (node.dest.color < 0) {
                        curBlock.addInst(new SEQZ("t0", "t0"));
                        storeReg(node.dest, "t0");
                    }
                }
            } else {
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    addXORI("t0", r_name, ((Constant) node.lhs).value);
                    if (node.dest.color > 0) {
                        curBlock.addInst(new SEQZ(Reg.freeRegs[node.dest.color], "t0"));
                    } else if (node.dest.color < 0) {
                        curBlock.addInst(new SEQZ("t0", "t0"));
                        storeReg(node.dest, "t0");
                    }
                } else {
                    if (node.dest.color < 0) {
                        curBlock.addInst(new LI("t0", ((Constant) node.lhs).value == ((Constant) node.rhs).value ? 1L : 0L));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new LI(storeReg(node.dest, null),
                                ((Constant) node.lhs).value == ((Constant) node.rhs).value ? 1L : 0L));
                    }
                }
            }
        } else if (node.op.equals("ne")) {
            if (node.lhs instanceof Register l) {
                String l_name = fetchReg(l, "t1");
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    curBlock.addInst(new XOR("t0", l_name, r_name));
                    if (node.dest.color > 0) {
                        curBlock.addInst(new SNEZ(Reg.freeRegs[node.dest.color], "t0"));
                    } else if (node.dest.color < 0) {
                        curBlock.addInst(new SNEZ("t0", "t0"));
                        storeReg(node.dest, "t0");
                    }
                } else {
                    addXORI("t0", l_name, ((Constant) node.rhs).value);
                    if (node.dest.color > 0) {
                        curBlock.addInst(new SNEZ(Reg.freeRegs[node.dest.color], "t0"));
                    } else if (node.dest.color < 0) {
                        curBlock.addInst(new SNEZ("t0", "t0"));
                        storeReg(node.dest, "t0");
                    }
                }
            } else {
                if (node.rhs instanceof Register r) {
                    String r_name = fetchReg(r, "t2");
                    addXORI("t0", r_name, ((Constant) node.lhs).value);
                    if (node.dest.color > 0) {
                        curBlock.addInst(new SNEZ(Reg.freeRegs[node.dest.color], "t0"));
                    } else if (node.dest.color < 0) {
                        curBlock.addInst(new SNEZ("t0", "t0"));
                        storeReg(node.dest, "t0");
                    }
                } else {
                    if (node.dest.color < 0) {
                        curBlock.addInst(new LI("t0", ((Constant) node.lhs).value == ((Constant) node.rhs).value ? 1L : 0L));
                        storeReg(node.dest, "t0");
                    } else if (node.dest.color > 0) {
                        curBlock.addInst(new LI(storeReg(node.dest, null),
                                ((Constant) node.lhs).value != ((Constant) node.rhs).value ? 1L : 0L));
                    }
                }
            }
        } else {
            String l_name = null, r_name = null;
            if (node.lhs instanceof Register l) {
                l_name = fetchReg(l, "t1");
            } else {
                curBlock.addInst(new LI("t1", ((Constant) node.lhs).value));
                l_name = "t1";
            }
            if (node.rhs instanceof Register r) {
                r_name = fetchReg(r, "t2");
            } else {
                curBlock.addInst(new LI("t2", ((Constant) node.rhs).value));
                r_name = "t2";
            }
            if (node.op.equals("slt")) {
                if (node.dest.color > 0) {
                    curBlock.addInst(new SLT(storeReg(node.dest, null), l_name, r_name));
                } else if (node.dest.color < 0) {
                    curBlock.addInst(new SLT("t0", l_name, r_name));
                    storeReg(node.dest, "t0");
                }
            } else if (node.op.equals("sle")) {
                if (node.dest.color > 0) {
                    curBlock.addInst(new SLT(storeReg(node.dest, null), r_name, l_name));
                    addXORI(storeReg(node.dest, null), storeReg(node.dest, null), 1L);
                } else if (node.dest.color < 0) {
                    curBlock.addInst(new SLT("t0", r_name, l_name));
                    addXORI("t0", "t0", 1L);
                    storeReg(node.dest, "t0");
                }
            } else if (node.op.equals("sgt")) {
                if (node.dest.color > 0) {
                    curBlock.addInst(new SLT(storeReg(node.dest, null), r_name, l_name));
                } else if (node.dest.color < 0) {
                    curBlock.addInst(new SLT("t0", r_name, l_name));
                    storeReg(node.dest, "t0");
                }
            } else if (node.op.equals("sge")) {
                if (node.dest.color > 0) {
                    curBlock.addInst(new SLT(storeReg(node.dest, null), l_name, r_name));
                    addXORI(storeReg(node.dest, null), storeReg(node.dest, null), 1L);
                } else if (node.dest.color < 0) {
                    curBlock.addInst(new SLT("t0", l_name, r_name));
                    addXORI("t0", "t0", 1L);
                    storeReg(node.dest, "t0");
                }
            }
        }
    }

    @Override
    public void visit(Jmp node) {
        var mv = curIRBlock.mv.get(node.block.label);
        addMVList(mv);
        curBlock.addInst(new J(node.block.label));
    }


    @Override
    public void visit(Load node) {
        if (node.src.name.startsWith("@")) {
            if (node.dest.color < 0) {
                curBlock.addInst(new LWL("t0", node.src.name.substring(1)));
                storeReg(node.dest, "t0");
            } else if (node.dest.color > 0) {
                curBlock.addInst(new LWL(storeReg(node.dest, null), node.src.name.substring(1)));
            }
        } else {
            String sd = fetchReg(node.src, "t0");
            if (node.dest.color < 0) {
                addLW("t0", 0, sd);
                storeReg(node.dest, "t0");
            } else if (node.dest.color > 0) {
                addLW(storeReg(node.dest, "t0"), 0, sd);
            }
        }
    }

    @Override
    public void visit(Ret node) {
        if (node.type.typeName == null) {
            // return a null ptr
            curBlock.addInst(new LI("a0", 0));
        } else if (!node.type.typeName.equals("void")) {
            if (node.value instanceof Constant c) {
                curBlock.addInst(new LI("a0", c.value));
            } else if (node.value instanceof Register r) {
                String rn = r.name;
                if (rn.startsWith("@constStr-")) {
                    curBlock.addInst(new LA("a0", rn.substring(1).replace("-", "_")));
                } else {
                    if (r.color < 0) {
                        fetchReg(r, "a0");
                    } else if (r.color > 0) {
                        String n = fetchReg(r, null);
                        curBlock.addInst(new MV("a0", n));
                    } else if (r.name.endsWith(".val") || r.name.equals("%this1")) {
                        int i = curIRFuncDef.paramNames.indexOf(r.name);
                        if (i < 8) {
                            curBlock.addInst(new LW("a0", regPos.get(r.name), "sp"));
//                            if (i != 0) curBlock.addInst(new MV("a0", "a" + i));
                        } else {
                            curBlock.addInst(new LW("a0", curFunc.stackSize + (i - 8) * 4L, "sp"));
                        }
                    }
                }
            }
        }
        addLW("ra", curFunc.stackSize - 4, "sp");
        for (int i = 0; i < curIRFuncDef.regNum; ++i) {
            addLW("s" + i, curFunc.stackSize - 4L * (i + 2), "sp");
        }
        addADDI("sp", "sp", curFunc.stackSize);
        curBlock.addInst(new RET());
    }

        /*store
        if (node.cond instanceof Constant) {
            if (((Constant) node.cond).value == 0) {
                if (node.falseVal instanceof Constant) {
                    curBlock.addInst(new LI("t0", ((Constant) node.falseVal).value));
                } else {
                    addLW("t0", regPos.get(((Register) node.falseVal).name), "sp");
                }
                addSW("t0", regPos.get(node.dest.name), "sp"));
            } else {
                if (node.trueVal instanceof Constant) {
                    curBlock.addInst(new LI("t0", ((Constant) node.trueVal).value));
                } else {
                    addLW("t0", regPos.get(((Register) node.trueVal).name), "sp");
                }
                addSW("t0", regPos.get(node.dest.name), "sp"));
            }
        } else if (node.cond instanceof Register) {
            addLW("t0", regPos.get(((Register) node.cond).name), "sp");
            if (node.trueVal instanceof Constant) {
                curBlock.addInst(new LI("t1", ((Constant) node.trueVal).value));
            } else {
                addLW("t1", regPos.get(((Register) node.trueVal).name), "sp");
            }
            if (node.falseVal instanceof Constant) {
                curBlock.addInst(new LI("t2", ((Constant) node.falseVal).value));
            } else {
                addLW("t2", regPos.get(((Register) node.falseVal).name), "sp");
            }
            occSP += 4;
            regPos.put(node.dest.name, curFunc.stackSize - occSP);
            // omitted
    }*/

    @Override
    public void visit(Store node) {
        String valName = null;
        if (node.value instanceof Constant c) {
            curBlock.addInst(new LI("t0", c.value));
            valName = "t0";
            if (node.dest.name.startsWith("@")) {
                curBlock.addInst(new LA("t1", node.dest.name.substring(1)));
                addSW(valName, 0, "t1");
            } else {
                String d_name = fetchReg(node.dest, "t1");
                addSW(valName, 0, d_name);
            }
        } else if (node.value instanceof Register r) {
            if (r.name.endsWith(".val") || r.name.equals("%this1")) { // func params
                valName = fetchReg(r, "t0");
                if (node.dest.name.startsWith("@")) {
                    curBlock.addInst(new LA("t1", node.dest.name.substring(1)));
                    addSW(valName, 0, "t1");
                } else {
                    String d_name = fetchReg(node.dest, "t1");
                    addSW(valName, 0, d_name);
                }
            } else if (r.name.startsWith("@constStr-")) { // global ptrs
                curBlock.addInst(new LA("t0", r.name.substring(1).replace("-", "_")));
                if (node.dest.name.startsWith("@")) {
                    curBlock.addInst(new LA("t1", node.dest.name.substring(1)));
                    addSW("t0", 0, "t1");
                } else {
                    String d_name = fetchReg(node.dest, "t1");
                    addSW("t0", 0, d_name);
                }
            } else {
                valName = fetchReg(r, "t0");
                if (node.dest.name.startsWith("@")) {
                    curBlock.addInst(new LA("t1", node.dest.name.substring(1)));
                    addSW(valName, 0, "t1");
                } else {
                    String d_name = fetchReg(node.dest, "t1");
                    addSW(valName, 0, d_name);
                }
            }
        }
    }

    @Override
    public void visit(Tail node) {
        int funcParamNum = curIRFuncDef.paramNames.size();
        int cnt = 0;
        for (Entity e : node.args) {
            if (cnt < 8) {
                if (e instanceof Constant) {
                    curBlock.addInst(new LI("a" + cnt, ((Constant) e).value));
                } else if (e instanceof Register r) {
                    if (r.name.startsWith("%")) { // some variable
                        if (r.color < 0) {
                            addLW("a" + cnt, regPos.get(r.name), "sp");
                        } else if (r.color > 0) {
                            curBlock.addInst(new MV("a" + cnt, Reg.freeRegs[r.color]));
                        } else if (r.name.endsWith(".val") || r.name.equals("%this1")) {
                            int i = curIRFuncDef.paramNames.indexOf(r.name);
                            if (i < 8) {
                                curBlock.addInst(new LW("a" + cnt, regPos.get(r.name), "sp"));
//                                if (i >= cnt) {
//                                    // fetch from "a" reg
//                                    curBlock.addInst(new MV("a" + cnt, "a" + i));
//                                } else {
//                                    // fetch from stack
//                                    curBlock.addInst(new LW("a" + cnt, curFunc.stackSize - occSP + 4L * (funcParamNum - i - 1), "sp"));
//                                }
                            } else {
                                curBlock.addInst(new LW("a" + cnt, curFunc.stackSize + (i - 8) * 4L, "sp"));
                            }
                        }
                    } else {
                        curBlock.addInst(new LA("a" + cnt, ((Register) e).name.substring(1).replace("-", "_")));
                    }
                }
            } else {
                if (e instanceof Constant) {
                    curBlock.addInst(new LI("t0", ((Constant) e).value));
                    addSW("t0", (cnt - 8) * 4L, "sp");
                } else if (e instanceof Register r) {
                    String n = null;
                    if (r.name.startsWith("%")) {
                        n = fetchReg(r, "t0");
                    } else {
                        curBlock.addInst(new LA("t0", r.name.substring(1).replace("-", "_")));
                        n = "t0";
                    }
                    addSW(n, (cnt - 8) * 4L, "sp");
                }
            }
            ++cnt;
        }
        addLW("ra", curFunc.stackSize - 4, "sp");
        for (int i = 0; i < curIRFuncDef.regNum; ++i) {
            addLW("s" + i, curFunc.stackSize - 4L * (i + 2), "sp");
        }
        addADDI("sp", "sp", curFunc.stackSize);
        curBlock.addInst(new TAIL(node.funcName.substring(1)));
    }

    private void addSW(String src_, long offset_, String destAddr_) {
        assert !destAddr_.equals("sp") || offset_ >= 0;
        if (offset_ < 2048 && offset_ >= -2048) {
            curBlock.addInst(new SW(src_, offset_, destAddr_));
        } else {
            curBlock.addInst(new LI("tp", offset_));
            curBlock.addInst(new ADD("tp", "tp", destAddr_));
            curBlock.addInst(new SW(src_, 0, "tp"));
        }
    }

    private void addADDI(String dest_, String lhs_, long rhs_) {
        if (rhs_ < 2048 && rhs_ >= -2048) {
            curBlock.addInst(new ADDI(dest_, lhs_, rhs_));
        } else {
            curBlock.addInst(new LI("tp", rhs_));
            curBlock.addInst(new ADD(dest_, lhs_, "tp"));
        }
    }

    private void addXORI(String dest_, String lhs_, long rhs_) {
        if (rhs_ < 2048 && rhs_ >= -2048) {
            curBlock.addInst(new XORI(dest_, lhs_, rhs_));
        } else {
            curBlock.addInst(new LI("tp", rhs_));
            curBlock.addInst(new XOR(dest_, lhs_, "tp"));
        }
    }

    private void addORI(String dest_, String lhs_, long rhs_) {
        if (rhs_ < 2048 && rhs_ >= -2048) {
            curBlock.addInst(new ORI(dest_, lhs_, rhs_));
        } else {
            curBlock.addInst(new LI("tp", rhs_));
            curBlock.addInst(new OR(dest_, lhs_, "tp"));
        }
    }

    private void addLW(String dest_, long offset_, String srcAddr_) {
        assert !srcAddr_.equals("sp") || offset_ >= 0;
        if (offset_ < 2048 && offset_ >= -2048) {
            curBlock.addInst(new LW(dest_, offset_, srcAddr_));
        } else {
            curBlock.addInst(new LI("tp", offset_));
            curBlock.addInst(new ADD("tp", "tp", srcAddr_));
            curBlock.addInst(new LW(dest_, 0, "tp"));
        }
    }

    private void addANDI(String dest_, String lhs_, long rhs_) {
        if (rhs_ < 2048 && rhs_ >= -2048) {
            curBlock.addInst(new ANDI(dest_, lhs_, rhs_));
        } else {
            curBlock.addInst(new LI("tp", rhs_));
            curBlock.addInst(new AND(dest_, lhs_, "tp"));
        }
    }

    public void addMVList(ArrayList<Move> moves) {
        ArrayList<MV> regMV = new ArrayList<>();
        ArrayList<MV> immMV = new ArrayList<>();
        HashMap<Operand, HashSet<MV>> srcs = new HashMap<>();
        HashMap<Operand, HashSet<MV>> dests = new HashMap<>();
        for (var mv : moves) {
            MV m = new MV();
            if (mv.dest.color < 0) {
                if (regPos.containsKey(mv.dest.name)) {
                    m.dest = Stk.get(regPos.get(mv.dest.name));
                } else {
                    if (mv.coalesce && (mv.src instanceof Register r && r.color < 0)) {
                        int p = regPos.get(r.name);
                        m.dest = Stk.get(p);
                        regPos.put(mv.dest.name, p);
                    } else {
                        occSP += 4;
                        regPos.put(mv.dest.name, curFunc.stackSize - occSP);
                        m.dest = Stk.get(curFunc.stackSize - occSP);
                    }
                }
            } else if (mv.dest.color > 0) {
                m.dest = Reg.get(Reg.freeRegs[mv.dest.color]);
            }
            if (mv.src instanceof Register r) {
                if (r.color < 0) {
                    m.src = Stk.get(regPos.get(r.name));
                } else if (r.color > 0) {
                    m.src = Reg.get(Reg.freeRegs[r.color]);
                } else if (r.name.endsWith(".val") || r.name.equals("%this.val")) {
                    int i = curIRFuncDef.paramNames.indexOf(r.name);
                    if (i < 8) {
                        m.src = Stk.get(regPos.get(r.name));
//                        m.src = Reg.get("a" + i);
                    } else {
                        m.src = Stk.get(curFunc.stackSize + (i - 8) * 4L);
                    }
                } else if (r.name.startsWith("@constStr")) {
                    m.src = new Gl(r.name.substring(1).replace("-", "_"));
                }
            } else if (mv.src instanceof Constant c) {
                m.src = new Imm(c.value);
            }
            if (m.src instanceof Reg rs && m.dest instanceof Reg rd) {
                if (rs == rd) continue;
            }
            if (m.src instanceof Stk ss && m.dest instanceof Stk sd) {
                if (ss.pos == sd.pos) continue;
            }
            if (m.src instanceof Imm || m.src instanceof Gl) {
                immMV.add(m);
            } else {
                regMV.add(m);
                if (!srcs.containsKey(m.src)) {
                    srcs.put(m.src, new HashSet<>());
                }
                srcs.get(m.src).add(m);
                if (!dests.containsKey(m.dest)) {
                    dests.put(m.dest, new HashSet<>());
                }
                dests.get(m.dest).add(m);
                assert dests.get(m.dest).size() == 1;
            }
        }
        addRegMV(regMV, srcs, dests);
        addImmMV(immMV);


//        for (var mv : moves) {
//            String src_name = null;
//            if (mv.src instanceof Register r) {
//                src_name = fetchReg(r, "t0");
//                if (mv.dest.color > 0) {
//                    String dest_name = storeReg(mv.dest, null);
//                    if (!src_name.equals(dest_name)) {
//                        curBlock.addInst(new MV(storeReg(mv.dest, null), src_name));
//                    }
//                } else if (mv.dest.color < 0) {
//                    storeReg(mv.dest, src_name);
//                }
//            } else if (mv.src instanceof Constant c) {
//                if (mv.dest.color > 0) {
//                    curBlock.addInst(new LI(storeReg(mv.dest, null), c.value));
//                } else if (mv.dest.color < 0) {
//                    curBlock.addInst(new LI("t0", c.value));
//                    storeReg(mv.dest, "t0");
//                }
//            }
//        }

    }

    static class mvNode {
        MV mv;
        HashSet<MV> preds;
        HashSet<MV> succs;

        public mvNode(MV mv) {
            this.mv = mv;
            preds = new HashSet<>();
            succs = new HashSet<>();
        }

        public void printErr() {
            PrintStream consolePrintStream = new PrintStream(new FileOutputStream(FileDescriptor.err));
            System.setOut(consolePrintStream);
            System.out.println("------------");
            mv.print();
            System.out.println("pred:");
            preds.forEach(MV::print);
            System.out.println("succ:");
            succs.forEach(MV::print);
            System.out.println("------------");
            consolePrintStream = new PrintStream(new FileOutputStream(FileDescriptor.out));
            System.setOut(consolePrintStream);
        }
    }

    public void addRegMV(ArrayList<MV> moves, HashMap<Operand, HashSet<MV>> srcs, HashMap<Operand, HashSet<MV>> dests) {
        // observation: there is no same-dest operation -> at most one succ
        // corollary1: a node can only participate in one ring
        // corollary2: no node depends on nodes that is on a ring
        if (moves.isEmpty()) return;
        HashMap<MV, mvNode> m = new HashMap<>();
        ArrayDeque<mvNode> zeroin = new ArrayDeque<>();
        for (var mv : moves) {
            m.put(mv, new mvNode(mv));
        }
        for (Map.Entry<Operand, HashSet<MV>> s : srcs.entrySet()) {
            HashSet<MV> l = dests.get(s.getKey());
            if (l != null) {
                for (MV mv : s.getValue()) {
                    m.get(mv).succs.addAll(l);
                }
            }
        }
        for (Map.Entry<Operand, HashSet<MV>> d : dests.entrySet()) {
            HashSet<MV> l = srcs.get(d.getKey());
            for (MV mv : d.getValue()) {
                if (l == null) {
                    zeroin.add(m.get(mv));
                    continue;
                }
                m.get(mv).preds.addAll(l);
                if (l.size() == 0) {
                    zeroin.add(m.get(mv));
                }
            }
        }
        while (!m.isEmpty()) {
            while (!zeroin.isEmpty()) {
                mvNode n = zeroin.poll();
                m.remove(n.mv);
                addMV(n.mv);
//                if (curBlock.label.equals("l_4_22_8_for_step")) {
//                    n.printErr();
//                }
                for (var suc : n.succs) {
                    mvNode sucn = m.get(suc);
                    sucn.preds.remove(n.mv);
                    if (sucn.preds.isEmpty()) {
                        zeroin.add(sucn);
                    }
                }
            }
            if (m.isEmpty()) break;
            mvNode br = m.entrySet().iterator().next().getValue();
            // from corollary 2, whatever node we pick, it is in a ring
            MV nmv = new MV();
            nmv.dest = Reg.get("t0");
            nmv.src = br.mv.dest;
            mvNode n = new mvNode(nmv);
            br.preds.iterator().next().src = Reg.get("t0");
            HashSet<MV> ps = m.get(br.preds.iterator().next()).succs;
            n.succs = new HashSet<>(ps);
            ps.clear();
            br.preds.clear();
            br.preds.add(nmv);
            m.put(nmv, n);
            zeroin.add(n);
        }

    }

    public void addImmMV(ArrayList<MV> moves) {
        for (var mv : moves) {
            if (mv.src instanceof Imm i) {
                long val = i.value;
                if (mv.dest instanceof Reg r) {
                    curBlock.addInst(new LI(r.name, val));
                } else if (mv.dest instanceof Stk s) {
                    curBlock.addInst(new LI("t0", val));
                    addSW("t0", s.pos, "sp");
                }
            } else if (mv.src instanceof Gl g) {
                String val = g.label;
                if (mv.dest instanceof Reg r) {
                    curBlock.addInst(new LA(r.name, val));
                } else if (mv.dest instanceof Stk s) {
                    curBlock.addInst(new LA("t0", val));
                    addSW("t0", s.pos, "sp");
                }
            }
        }
    }

    public void addMV(MV mv) {
        if (mv.src instanceof Imm i) {
            if (mv.dest instanceof Reg r) {
                curBlock.addInst(new LI(r.name, i.value));
            } else if (mv.dest instanceof Stk s) {
                curBlock.addInst(new LI("t0", i.value));
                addSW("t0", s.pos, "sp");
            }
        } else if (mv.src instanceof Reg rs) {
            if (mv.dest instanceof Reg r) {
                if (r.name.equals(rs.name)) return;
                curBlock.addInst(new MV(r.name, rs.name));
            } else if (mv.dest instanceof Stk s) {
                addSW(rs.name, s.pos, "sp");
            }
        } else if (mv.src instanceof Stk ss) {
            if (mv.dest instanceof Reg r) {
                addLW(r.name, ss.pos, "sp");
            } else if (mv.dest instanceof Stk s) {
                if (s.pos == ss.pos) return;
                addLW("t0", ss.pos, "sp");
                addSW("t0", s.pos, "sp");
            }
        }
    }

}
