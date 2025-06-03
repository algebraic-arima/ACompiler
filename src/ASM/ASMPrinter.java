package src.ASM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ASMPrinter {

    ASMProg prog;

    public ASMPrinter(ASMProg prog) {
        this.prog = prog;
    }

    public void print() {

//        String filePath = "src/builtin/builtin.s";
//
//        StringBuilder fileContent = new StringBuilder();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                fileContent.append(line).append(System.lineSeparator());
//            }
//        } catch (IOException e) {
//            return;
//        }
//        System.out.print(fileContent);
//        System.out.print("\n");
        prog.print();
    }

}
