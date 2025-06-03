package src.ASM.ASMDef;

import src.ASM.ASMNode;

public class ASMGStrDef extends ASMNode {
    // use .ascii

    public String name;
    public String value;
    public int len;

    public ASMGStrDef(String name, String value) {
        this.name = name;
        this.len = value.length() + 1;
        this.value = value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    @Override
    public void print() {
        System.out.println(name + ":");
        System.out.println("  .asciz \"" + value + "\"");
        System.out.println("  .size " + name + ", " + len + "\n");
    }
}
