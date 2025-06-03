package src.utils.Entity;

import src.AST.BlakeHasher;
import src.utils.IRType.IRType;
import src.utils.type.Type;

abstract public class Entity {
    public IRType type;

    static public IRType typeI1=new IRType("bool");
    static public IRType typeI32=new IRType("int");
    static public IRType typePtr=new IRType("ptr");


    public Entity() {
        this.type = new IRType();
    }

    public Entity(IRType type) {
        this.type = type;
    }

    abstract public void print();

    @Override
    public abstract boolean equals(Object obj);

}
