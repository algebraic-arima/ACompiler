package src.utils.Entity;

import src.utils.IRType.IRType;

import java.util.HashMap;

public class Register extends Entity {

    public String name = null;
    public int color;

    static public HashMap<String, Register> registers = new HashMap<>();

    public Register() {
        super();
    }

    public Register(String name) {
        this.name = name;
    }

    @Override
    public void print() {
        System.out.print(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Register r) {
            return r == this;
        }
        return false;
    }

    public Register(IRType type, String name) {
        super(type);
        this.name = name;
    }

    static public Register newReg(String name) {
        if (registers.containsKey(name)) {
            return registers.get(name);
        }
        Register reg = new Register(name);
        registers.put(name, reg);
        return reg;
    }

    static public Register newReg(IRType type, String name) {
        if (registers.containsKey(name)) {
            return registers.get(name);
        }
        Register reg = new Register(type, name);
        registers.put(name, reg);
        return reg;
    }

    static public void markStack(String name, int c) {
        registers.get(name).color = -c;
    }

    static public void markReg(String name, int c) {
        registers.get(name).color = c;
    }

}
