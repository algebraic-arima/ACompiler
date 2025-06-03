package src.Optim.SCCP;

import src.IR.IRDef.IRBlock;

public class Edge extends SCCPNode {
    public IRBlock from, to;
    public boolean pass = false;

    public Edge(IRBlock f, IRBlock t) {
        from = f;
        to = t;
    }
}
