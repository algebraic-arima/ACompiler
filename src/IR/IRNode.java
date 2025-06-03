package src.IR;

import src.utils.Entity.Register;

abstract public class IRNode {
    abstract public void accept(IRVisitor visitor);
}
