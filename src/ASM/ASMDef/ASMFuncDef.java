package src.ASM.ASMDef;

import src.ASM.ASMNode;

import java.util.ArrayList;

public class ASMFuncDef extends ASMNode {

    public String name;
    public ArrayList<ASMBlock> blocks;
    public int stackSize; // in byte
    public String className = null; // = null if not a method
    public String funcName;
    public String hash;

    public ASMFuncDef(String name) {
        this.name = name;
        blocks = new ArrayList<>();
    }

    @Override
    public void print() {
        System.out.println("  .globl " + name);
        System.out.println("  .type " + name + ", @function");
        blocks.forEach(ASMBlock::print);
    }
}
