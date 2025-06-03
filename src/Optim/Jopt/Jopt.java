package src.Optim.Jopt;

import src.ASM.ASMDef.ASMBlock;
import src.ASM.ASMDef.ASMFuncDef;
import src.ASM.ASMInst.ASMInst;
import src.ASM.ASMInst.BR.BR;
import src.ASM.ASMInst.BR.J;
import src.ASM.ASMProg;

import java.util.ArrayList;
import java.util.HashMap;

public class Jopt {

    public ASMProg prog;
    public ArrayList<ASMInst> assembly = new ArrayList<>();
    public HashMap<String, Long> pos = new HashMap<>();
    public HashMap<String, ASMBlock> blockList = new HashMap<>();
    public HashMap<String, String> trimList = new HashMap<>();

    public Jopt(ASMProg _prog) {
        prog = _prog;
        collectPos();
        jumpSim();
        reformat();
    }

    public void collectPos() {
        for (ASMFuncDef f : prog.funcDefs) {
            for (ASMBlock b : f.blocks) {
                pos.put(b.label, (long) assembly.size());
                blockList.put(b.label, b);
                assembly.addAll(b.insts);
                if (b.insts.size() == 1 && b.insts.getFirst() instanceof BR br
                        && !b.label.equals(br.label)) {
                    trimList.put(b.label, br.label);
                }
            }
        }
    }

    public void jumpSim() {
        for (int i = 0; i < assembly.size(); ++i) {
            ASMInst inst = assembly.get(i);
            if (inst instanceof BR br && !(inst instanceof J)) {
                if (trimList.containsKey(br.label)) {
                    String repDest = trimList.get(br.label);
                    long offset = pos.get(repDest) - i;
                    if (offset < 1024 && offset >= -1024) {
                        br.label = trimList.get(br.label);
                        --i;
                    }
                }
            }
        }
    }

    public void reformat() {
        for (ASMFuncDef f : prog.funcDefs) {
            for (int i = 0; i < f.blocks.size() - 1; ++i) {
                ASMBlock b = f.blocks.get(i);
                ASMBlock bn = f.blocks.get(i + 1);
                if (b.insts.getLast() instanceof J j && j.label.equals(bn.label)) {
                    b.insts.removeLast();
                }
            }
        }
    }

}
