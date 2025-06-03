package src.utils.Entity;

import src.utils.IRType.IRType;

import java.util.HashMap;

public class Constant extends Entity {
    public long value;

    public static HashMap<Long, Integer> log2n;

    static {
        log2n = new HashMap<>();
        long p = 1;
        for (int i = 0; i < 30; ++i) {
            log2n.put(p, i);
            p *= 2;
        }
    }

    public String getName() {
        return null;
    }

    @Override
    public void print() {
        if (type.typeName.equals("ptr")) {
            System.out.print("null");
        } else {
            System.out.print(value);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Constant c) {
            return c.value == value;
        }
        return false;
    }

    public Constant(long d) {
        super();
        value = d;
        type = typeI32;
    }

    public Constant(long d, boolean isBool) {
        super();
        value = d;
        type = typeI1;
    }

    public Constant(boolean isNull) {
        super();
        value = 0;
        type = typePtr;
    }

    public int log2() {
        return log2n.getOrDefault(value, -1);
    }
}
