package src.AST;

import io.github.rctcwyvrn.blake3.Blake3;
import src.AST.Def.Def;

import src.AST.Expr.Expr;
import src.AST.Stmt.Stmt;
import src.utils.type.Type;

import java.util.ArrayList;


public class BlakeHasher {
    private final Blake3 hasher;

    public BlakeHasher() {
        this.hasher = Blake3.newInstance();
    }

    public void update(Object... args) {
        for (Object arg : args) {
            byte[] incre;
            switch (arg) {
                case null -> {
                    Blake3 b = Blake3.newInstance();
                    b.update("null".getBytes());
                    incre = b.hexdigest().getBytes();
                }
                case String s -> {
                    Blake3 b = Blake3.newInstance();
                    b.update(s.getBytes());
                    incre = b.hexdigest().getBytes();
                }
                case Def def -> {
                    def.setHash();
                    incre = def.hash.getBytes();
                }
                case Stmt stmt -> {
                    stmt.setHash();
                    incre = stmt.hash.getBytes();
                }
                case Expr expr -> {
                    expr.setHash();
                    incre = expr.hash.getBytes();
                }
                case Type t -> {
                    t.setHash();
                    incre = t.hash.getBytes();
                }
                case Long l -> {
                    Blake3 b = Blake3.newInstance();
                    b.update(l.toString().getBytes());
                    incre = b.hexdigest().getBytes();
                }
                case Integer i -> {
                    Blake3 b = Blake3.newInstance();
                    b.update(i.toString().getBytes());
                    incre = b.hexdigest().getBytes();
                }
                default -> throw new IllegalArgumentException("Unsupported type: " + arg.getClass());
            }
            hasher.update(incre);
        }
    }

    public void unorderedMix(ArrayList<Object> args) {
        byte[] ans = new byte[64];
        for (Object arg : args) {
            byte[] incre;
            switch (arg) {
                case null -> {
                    Blake3 b = Blake3.newInstance();
                    b.update("null".getBytes());
                    incre = b.hexdigest().getBytes();
                }
                case String s -> {
                    Blake3 b = Blake3.newInstance();
                    b.update(s.getBytes());
                    incre = b.hexdigest().getBytes();
                }
                case Def def -> {
                    def.setHash();
                    incre = def.hash.getBytes();
                }
                case Stmt stmt -> {
                    stmt.setHash();
                    incre = stmt.hash.getBytes();
                }
                case Expr expr -> {
                    expr.setHash();
                    incre = expr.hash.getBytes();
                }
                case Type t -> {
                    t.setHash();
                    incre = t.hash.getBytes();
                }
                case Long l -> {
                    Blake3 b = Blake3.newInstance();
                    b.update(l.toString().getBytes());
                    incre = b.hexdigest().getBytes();
                }
                case Integer i -> {
                    Blake3 b = Blake3.newInstance();
                    b.update(i.toString().getBytes());
                    incre = b.hexdigest().getBytes();
                }
                default -> throw new IllegalArgumentException("Unsupported type: " + arg.getClass());
            }
            for (int i = 0; i < 64; ++i){
                ans[i] = (byte) (ans[i] ^ incre[i]);
            }
        }
        hasher.update(ans);
    }

    public String hexdigest() {
        return hasher.hexdigest();
    }
}
