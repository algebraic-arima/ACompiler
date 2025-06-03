package src.IR;

public class IRPrinter {

    public IRProg irProgram;

    public IRPrinter(IRProg irProgram) {
        this.irProgram = irProgram;
    }

    public void print() {
        printOut("declare dso_local void @print(ptr)\n",
                "declare dso_local void @println(ptr)\n",
                "declare dso_local void @printInt(i32)\n",
                "declare dso_local void @printlnInt(i32)\n",
                "declare dso_local ptr @getString()\n",
                "declare dso_local i32 @getInt()\n",
                "declare dso_local ptr @toString(i32)\n",
                "declare dso_local i32 @string..length(ptr)\n",
                "declare dso_local ptr @string..substring(ptr,i32,i32)\n",
                "declare dso_local i32 @string..parseInt(ptr)\n",
                "declare dso_local i32 @string..ord(ptr,i32)\n",
                "declare dso_local ptr @string..add(ptr,ptr)\n",
                "declare dso_local i1 @string..eq(ptr,ptr)\n",
                "declare dso_local i1 @string..ne(ptr,ptr)\n",
                "declare dso_local i1 @string..lt(ptr,ptr)\n",
                "declare dso_local i1 @string..le(ptr,ptr)\n",
                "declare dso_local i1 @string..gt(ptr,ptr)\n",
                "declare dso_local i1 @string..ge(ptr,ptr)\n",
                "declare dso_local i32 @array..size(ptr)\n",
                "declare dso_local ptr @array..malloc(i32)\n",
                "declare dso_local ptr @..malloc(i32)\n\n");
        irProgram.print();
    }

    public void printOut(String... strings) {
        for (String string : strings) {
            System.out.print(string);
        }
    }

}
