package src.Cache;

import src.AST.Def.ClassDef;
import src.AST.Def.Def;
import src.AST.Def.FuncDef;
import src.AST.Expr.Expr;
import src.AST.Prog;
import src.AST.Stmt.ExprStmt;
import src.AST.Stmt.Stmt;
import src.cAST.CProg;
import src.cAST.Def.CDef;
import src.cAST.Def.ClassCDef;
import src.cAST.Def.FuncCDef;
import src.cAST.Expr.CExpr;
import src.cAST.Stmt.CStmt;
import src.cAST.Stmt.ExprCStmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Diff {
    Prog curProg;
    CProg cacheProg;
    public HashMap<String, FuncDef> stable = new HashMap<>();
    public ArrayList<FuncDef> compile = new ArrayList<>();

    public Diff(Prog cur, CProg cache) {
        this.curProg = cur;
        this.cacheProg = cache;
        funcHit();
    }

    public void funcHit() {
        for (Def d : curProg.defs) {
            if (d instanceof FuncDef funcDef) {
                CDef def = cacheProg.findCachebyName(funcDef.funcName);
                if (def != null) {
                    if (def instanceof FuncCDef f) {
                        if (f.hash.equals(funcDef.hash)) {
                            stable.put(funcDef.hash, funcDef);
                            funcDef.setNoCompile();
                        }
                    } else {
                        throw new RuntimeException("Cache function definition type mismatch!");
                    }
                } else {
                    compile.add(funcDef);
                }
            } else if (d instanceof ClassDef classDef) {
                CDef def = cacheProg.findCachebyName(classDef.className);
                if (def != null) {
                    if (def instanceof ClassCDef classCDef) {
                        if (classCDef.hash.equals(classDef.hash)) {
                            for (FuncDef funcDef : classDef.classFunc) {
                                stable.put(funcDef.hash, funcDef);
                                funcDef.setNoCompile();
                            }
                        }
                    } else {
                        throw new RuntimeException("Cache class definition type mismatch!");
                    }
                } else {
                    for (FuncDef funcDef : classDef.classFunc) {
                        CDef funcCDef = cacheProg.findCachebyName(classDef.className + "::" + funcDef.funcName);
                        if (funcCDef != null) {
                            if (funcCDef instanceof FuncCDef f) {
                                if (f.hash.equals(funcDef.hash)) {
                                    stable.put(funcDef.hash, funcDef);
                                    funcDef.setNoCompile();
                                }
                            } else {
                                throw new RuntimeException("Cache function definition type mismatch!");
                            }
                        } else {
                            compile.add(funcDef);
                        }
                    }
                }
            }
        }
    }

    public HashMap<CExpr, Expr> diffFuncBody(FuncDef funcDef, FuncCDef funcCDef) {
        if (funcDef.funcBody.stmts.size() != funcCDef.funcBody.CStmts.size()) {
            return null;
        }
        ArrayList<Stmt> curBody = funcDef.funcBody.stmts;
        ArrayList<CStmt> cacheBody = funcCDef.funcBody.CStmts;
        HashMap<Expr, CExpr> diffStmts = new HashMap<>();
        int i = 0;
        while (i < curBody.size()) {
            Stmt curStmt = curBody.get(i);
            CStmt cacheStmt = cacheBody.get(i);
            if (!curStmt.hash.equals(cacheStmt.hash)) {
                if (curStmt instanceof ExprStmt e && cacheStmt instanceof ExprCStmt c) {
                    diffStmts.put(e.expr, c.CExpr);
                } else {
                    return null;
                }
            }
            i++;
        }
        HashMap<CExpr, Expr> diff = new HashMap<>();
        for (Map.Entry<Expr, CExpr> entry : diffStmts.entrySet()) {
            Expr expr = entry.getKey();
            CExpr cExpr = entry.getValue();
            diff.putAll(diffExpr(expr, cExpr));
        }
        if (diff.isEmpty()) {
            return null;
        } else {
            return diff;
        }
    }

    public HashMap<CExpr, Expr> diffExpr(Expr expr, CExpr cExpr) {
        if (expr.hash.equals(cExpr.hash)) {
            return null;
        }
        return cExpr.diff(expr);
    }
}
