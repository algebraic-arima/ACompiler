package src.ASM.ASMDef;

import src.ASM.ASMNode;

public class ASMGVarDef extends ASMNode {

    public String name;
    public long value;

    public ASMGVarDef(String name, long value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void print() {
        System.out.println("  .globl " + name);
        System.out.println(name + ":");
        System.out.println("  .word " + value + "\n");
    }
}
