package src.cAST;

import src.cAST.Def.Def;

import java.util.ArrayList;

public class CProg extends BaseCASTNode {
    public ArrayList<Def> defs;
    public ArrayList<String> funcHash = new ArrayList<>();
}