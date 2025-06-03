package src.ASM.Operand;

public class Gl extends Operand {
    public String label;

    public Gl(String std) {
        this.label = std;
    }

    @Override
    public String toString(){
        return label;
    }
}
