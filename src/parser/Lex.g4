lexer grammar Lex;

@header{
    package src.parser;
}

USE: 'use';

VOID: 'void';
BOOL: 'bool';
INT: 'int';
STRING: 'string';
NEW: 'new';
CLASS: 'class';
NULL: 'null';
TRUE: 'true';
FALSE: 'false';
THIS: 'this';
IF: 'if';
ELSE: 'else';
FOR: 'for';
WHILE: 'while';
BREAK: 'break';
CONTINUE: 'continue';
RETURN: 'return';

// omitted
WS: [ \t\r\n]+ -> skip;
CMT: ('//'.*?'\n'|'/*'.*?'*/') -> skip;

LPAR: '(';
RPAR: ')';
LBKT: '[';
RBKT: ']';
LBCE: '{';
RBCE: '}';

QUES: '?';
COLON: ':';
DCOLON: '::';

SEMI: ';';
COMMA: ',';

ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
MOD: '%';

LT: '<';
GT: '>';
LEQ: '<=';
GEQ: '>=';
EQ: '==';
NEQ: '!=';

AND: '&&';
OR: '||';
NOT: '!';

BLS: '<<';
BRS: '>>';
BAND: '&';
BOR: '|';
BXOR: '^';
BNOT: '~';

ASSIGN: '=';

SINC: '++';
SDEC: '--';

MEMB: '.';

INTCONST: ([1-9][0-9]*)|'0';
STRINGCONST: '"' STRCHAR*? '"';

FMTSTRPURE:  'f"' FMTSTRCHAR* '"';
FMTSTREND: '$' FMTSTRCHAR* '"';
FMTSTRBODY: '$' FMTSTRCHAR* '$';
FMTSTRBGN: 'f"' FMTSTRCHAR* '$';

ID: [a-zA-Z][a-zA-Z_0-9]*;

fragment STRCHAR:
    ~[\u0000-\u001F\u007F"\\]
    | '\\\\'
    | '\\n'
    | '\\"'
    ;

fragment FMTSTRCHAR:
    ~[\u0000-\u001F\u007F"\\$]
    | '\\\\'
    | '\\n'
    | '\\"'
    | '$$'
    ;

