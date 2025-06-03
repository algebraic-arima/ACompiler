package src.Semantic;

import java.util.HashMap;

import src.utils.error.*;

public class SemanticJudge {

    public void catPrint(error e) {
        String s = e.message;
        switch (s) {
            case "function main not found":
            case "main function must return int type":
            case "main function must have no params":
                p("Main Function Error");
                break;
            case "no such return type":
            case "invalid variable definition: class not defined":
            case "FuncCall: function not defined":
            case "MemberFuncCall: class not defined":
            case "MemberFuncCall: method not defined":
            case "MemberObjAccessExpr: class not defined":
            case "MemberObjAccessExpr: field not defined":
            case "variable not defined":
                p("Undefined Identifier");
                break;
            case "non-void non-main function must have a return statement":
                p("Missing Return Statement");
                break;
            case "invalid variable definition: void type":
            case "if condition should be bool":
            case "while condition should be bool":
            case "for condition should be bool":
            case "index of array should be int":
            case "string invalid operation":
            case "bool invalid operation":
            case "array invalid operation":
            case "type invalid for binary expression":
            case "lhs of binary logic expr should be bool":
            case "rhs of binary logic expr should be bool":
            case "Formatted string: invalid type":
            case "MemberFuncCall: object should be class, array or string":
            case "MemberObjAccessExpr: object should be class":
            case "you should not new a void type":
            case "NewArrayExpr: dimension should be int":
            case "TernaryBranchExpr: condition should be bool":
            case "UnaryArithExpr should be int":
            case "UnaryLogicExpr should be bool":
                p("Invalid Type");
                break;
            case "variable definition: type not match":
            case "void function must not have return value":
            case "non-void function must have a return value":
            case "return type not match":
            case "AssignExpr: type not match":
            case "only '==' and '!=' can be used to compare an array and null":
            case "lhs and rhs type does not match":
            case "FuncCall: number of arguments not match":
            case "FuncCall: type of arguments not match":
            case "MemberFuncCall: number of arguments not match":
            case "MemberFuncCall: type of arguments not match":
            case "NewArrayInitExpr: dimension not match":
            case "TernaryBranchExpr: type not match":
            case "rvalue inc/dec by suffix":
                p("Type Mismatch");
                break;
            case "variable definition: variable already defined in current scope":
            case "function name used in class or function":
                p("Multiple Definitions");
                break;
            case "Break not in loop":
            case "Continue not in loop":
                p("Invalid Control Flow");
                break;
            case "array access on non-array":
                p("Dimension Out Of Bound");
                break;
            default:
                p(s);
        }
    }

    private void p(String s) {
        System.out.print(s);
    }

}
