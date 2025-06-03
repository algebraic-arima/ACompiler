package src.ASM.Operand;

public class Imm extends Operand{

    public long value;

    public Imm(long value) {
        super();
        this.value = value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
