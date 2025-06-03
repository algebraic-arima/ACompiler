package src.IR;

import src.IR.IRDec.IRFuncDec;
import src.IR.IRDec.IRGVarDec;
import src.IR.IRDef.*;
import src.IR.IRInst.*;

public interface IRVisitor {

    void visit(IRProg node);

    void visit(IRClassDef node);

    void visit(IRFuncDef node);

    void visit(IRGlobalVarDef node);

    void visit(IRStrDef node);

    void visit(IRFuncDec node);

    void visit(IRGVarDec node);

    void visit(IRBlock node);

    void visit(Alloca node);

    void visit(Binary node);

    void visit(Br node);

    void visit(Call node);

    void visit(GetElePtr node);

    void visit(Icmp node);

    void visit(Jmp node);

    void visit(Load node);

    void visit(Ret node);

    void visit(Store node);

    void visit(Tail node);


}
