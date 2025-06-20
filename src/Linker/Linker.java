package src.Linker;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Linker {
    ArrayList<File> srcFiles;
    String targetPath;
    StringBuilder textSection = new StringBuilder();
    StringBuilder dataSection = new StringBuilder();
    StringBuilder rodataSection = new StringBuilder();
    HashSet<String> funcList = new HashSet<>();
    HashSet<String> callList = new HashSet<>();

    public Linker(ArrayList<File> srcFiles, String targetPath) {
        this.srcFiles = srcFiles;
        this.targetPath = targetPath;
        addBuiltin();
        try {
            link();
        } catch (IOException e) {
            throw new RuntimeException("Linking failed: " + e.getMessage(), e);
        }
    }

    void addBuiltin() {
        funcList.add("print");
        funcList.add("println");
        funcList.add("printInt");
        funcList.add("printlnInt");
        funcList.add("getString");
        funcList.add("getInt");
        funcList.add("toString");
        funcList.add("string..length");
        funcList.add("string..substring");
        funcList.add("string..parseInt");
        funcList.add("string..ord");
        funcList.add("string..add");
        funcList.add("string..eq");
        funcList.add("string..ne");
        funcList.add("string..lt");
        funcList.add("string..le");
        funcList.add("string..gt");
        funcList.add("string..ge");
        funcList.add("array..size");
        funcList.add("array..malloc");
        funcList.add("..malloc");
        funcList.add(".init");
    }

    void link() throws IOException {
        for (File file : srcFiles) {
            String pathName = file.getPath();
            String fileName = file.getName();
            pathName = pathName.substring(0, pathName.length() - 3);
            fileName = fileName.substring(0, fileName.length() - 3);
            String asmNames = pathName + ".s";
            File asm = new File(asmNames);
            assert(asm.exists());

            List<String> lines = Files.readAllLines(asm.toPath());

            String currentSection = null;

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.startsWith(".section")) {
                    if (trimmed.contains(".text")) {
                        currentSection = ".text";
                    } else if (trimmed.contains(".data")) {
                        currentSection = ".data";
                    } else if (trimmed.contains(".rodata")) {
                        currentSection = ".rodata";
                    } else {
                        currentSection = null;
                    }
                    continue;
                }

                if (currentSection == null) continue;


                if (line.endsWith("  .globl .init")) {
                    line = "  .globl .init.." + fileName;
                } else if (line.endsWith("  .type .init, @function")) {
                    line = "  .type .init.." + fileName + ", @function";
                } else if (line.endsWith(".init:")) {
                    line = ".init.." + fileName + ":";
                }

                switch (currentSection) {
                    case ".text" -> textSection.append(line).append("\n");
                    case ".data" -> dataSection.append(line).append("\n");
                    case ".rodata" -> rodataSection.append(line).append("\n");
                }


                if (line.startsWith("  .globl ")) {
                    addFunc(line.substring(9));
                }

                if (line.startsWith("  call ")) {
                    addCall(line.substring(7));
                }
            }
        }
        for (String s : callList) {
            if (!funcList.contains(s)) {
                throw new RuntimeException("linker: no definition found");
            }
        }

        String curInitPre = "  .globl .init\n" +
                "  .type .init, @function\n" +
                ".init:\n" +
                "  addi sp, sp, -16\n" +
                "  sw ra, 12(sp)\n";
        String curInitPost = "  lw ra, 12(sp)\n" +
                "  addi sp, sp, 16\n" +
                "  ret";
//        textSection.append("\n").append(curInitPre).append("\n");
//        for (File file : srcFiles) {
//            String fileName = file.getName();
//            fileName = fileName.substring(0, fileName.length() - 2);
//            textSection.append("  call .init..").append(fileName).append("\n");
//        }
//        textSection.append(curInitPost).append("\n");

        writeOutput();
    }

    void addFunc(String s) {
        if (funcList.contains(s)) {
            throw new RuntimeException("linker: symbol multiple definition");
        } else {
            funcList.add(s);
        }
    }

    void addCall(String s) {
        callList.add(s);
    }

    void writeOutput() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetPath))) {
            if (textSection.length() > 0) {
                writer.write(".section .text\n");
                writer.write(textSection.toString());
            }
            if (dataSection.length() > 0) {
                writer.write("\n.section .data\n");
                writer.write(dataSection.toString());
            }
            if (rodataSection.length() > 0) {
                writer.write("\n.section .rodata\n");
                writer.write(rodataSection.toString());
            }
        }
    }
}
