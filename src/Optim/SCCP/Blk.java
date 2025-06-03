package src.Optim.SCCP;

import src.IR.IRDef.IRBlock;

import java.util.ArrayList;

public class Blk extends SCCPNode {
    public boolean exec = false;
    public IRBlock blk;
    public ArrayList<Blk> pred, succ;

    public Blk(IRBlock b) {
        blk = b;
        pred = new ArrayList<>();
        succ = new ArrayList<>();
    }
}
