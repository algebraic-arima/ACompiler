parser grammar Mx;

@header{
    package src.parser;
}

options {
    tokenVocab = Lex;
}

prog: (def | dec)+ EOF? ;

def: funcdef | classdef | (vardef SEMI) ;

funcdef: type ID LPAR (type ID (COMMA type ID)*)? RPAR stmts ;

classdef: CLASS ID LBCE ((vardef SEMI) | funcdef)* constructor? ((vardef SEMI) | funcdef)* RBCE SEMI ;

vardef: type singlevardef (COMMA singlevardef)* ;

dec: gvardec | gfuncdec | classdec | fieldec | methodec ;

gvardec: USE type ID (COMMA ID)* SEMI ;

gfuncdec: USE type ID LPAR (type (COMMA type)*)? RPAR SEMI ;

classdec: USE ID SEMI ; // to import constructor

fieldec: USE type ID DCOLON ID SEMI ; // to enable use of class member

methodec: USE type ID DCOLON ID LPAR (type (COMMA type)*)? RPAR SEMI ; // to import method

singlevardef: ID (ASSIGN expr)?;

constructor: ID LPAR RPAR stmts;

stmts: LBCE stmt* RBCE ;

jmpstmt:
    IF LPAR expr RPAR truestmt=stmt (ELSE falsestmt=stmt)? # IfStmt
    | BREAK SEMI # BreakStmt
    | CONTINUE SEMI # ContinueStmt
    | RETURN expr? SEMI # ReturnStmt
    ;

lpstmt:
    FOR LPAR (rowexpr | vardef)? SEMI cond=expr? SEMI update=expr? RPAR stmt # ForStmt
    | WHILE LPAR expr RPAR stmt # WhileStmt
    ;

stmt:
    stmts # BlockStmt
    | (vardef SEMI) # VarDefStmt
    | expr SEMI # ExprStmt
    | lpstmt # LoopStmt
    | jmpstmt # JumpStmt
    | SEMI # EmptyStmt
    ;

expr:
    LPAR expr RPAR # Parentheses
    | expr MEMB ID LPAR rowexpr? RPAR # MemberFuncCall
    | expr MEMB ID # MemberObjAccess
    | expr LBKT expr RBKT # ArrayAccess
    | ID LPAR rowexpr? RPAR # FuncCall
    | expr op=(SDEC|SINC) # RUnaryExp
    | op=(SDEC|SINC) expr # LUnaryExp
    | op=(SUB|NOT|BNOT) expr # LUnaryExp
    | expr op=(DIV|MUL|MOD) expr # BinaryExp
    | expr op=(ADD|SUB) expr # BinaryExp
    | expr op=(BLS|BRS) expr # BinaryExp
    | expr op=(LT|GT|LEQ|GEQ) expr # BinaryExp
    | expr op=(EQ|NEQ) expr # BinaryExp
    | expr op=BAND expr # BinaryExp
    | expr op=BXOR expr # BinaryExp
    | expr op=BOR expr # BinaryExp
    | expr op=AND expr # BinaryExp
    | expr op=OR expr # BinaryExp
    | <assoc = right> expr op=QUES expr op=COLON expr # TernaryExp
    | <assoc = right> expr op=ASSIGN expr # AssignExp
    | NEW singletype (LBKT expr RBKT)* (LBKT RBKT)*  arrayliteral? # NewArray
    | NEW ID (LPAR RPAR)? # NewClass
    | THIS # ThisPtr
    | ID # VarAccess
    | literal # Constant
    | fmtstr # FormatString
    ;

rowexpr: expr (COMMA expr)*;

type: singletype | arraytype;

singletype:
    ( BOOL | INT | STRING | VOID ) # BaseType
    | ID # ClassType
    ;

arraytype: singletype (LBKT RBKT)+ ;

literal: INTCONST | STRINGCONST | (TRUE | FALSE) | NULL | arrayliteral;
arrayliteral: LBCE rowexpr? RBCE ;

fmtstr: (FMTSTRBGN (expr (FMTSTRBODY expr)*) FMTSTREND) | FMTSTRPURE ;
