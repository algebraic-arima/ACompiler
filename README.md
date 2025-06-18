# Incremental Compilation: An Advanced Fake Mx* Compiler

## Goal

- [ ] Function-Level detect and recompile
- [ ] Expression-Level replacement
- [ ] Symbol renaming detect
- - [ ] variable rename
- - [ ] class rename
- [ ] Remote

## Usage

Place `utils.bash` under the same directory before running the bash.

```bash
find src -name '*.java' | xargs javac -d bin -cp /usr/local/bin/antlr-4.13.2-complete.jar
# for single test
${YOUR_PRIJECT_DIR}/test_asm_reimu.bash 'java -cp ${ANTLR_DIR}:bin src.Main -S' ${TESTCASE} ${BUILTIN}
# for efficient test
${YOUR_PROJECT_DIR}/test_asm_all_reimu.bash 'java -cp ${ANTLR_DIR}:bin src.Main -S' ${TESTCASE_DIR} ${BUILTIN}
```

Example
```bash
~/Git/compiler-2025/test_asm_reimu.bash 'java -cp /usr/local/bin/antlr-4.13.2-complete.jar:bin src.Main -S' testcases/codegen/optim/binary_tree.mx src/builtin/builtin.s
```

```bash
~/Git/compiler-2025/test_asm_all_reimu.bash 'java -cp /usr/local/bin/antlr-4.13.2-complete.jar:bin src.Main -S' testcases/codegen src/builtin/builtin.s
```

```bash
~/Git/compiler-2025/testcases/codegen/scripts/test_llvm_ir.bash 'java -cp /usr/local/bin/antlr-4.13.2-complete.jar:bin src.Main -S' testcases/codegen/optim/binary_tree.mx src/builtin/builtin.ll
```

```bash
~/Git/compiler-2025/testcases/codegen/scripts/test_llvm_ir_all.bash 'java -cp /usr/local/bin/antlr-4.13.2-complete.jar:bin src.Main -S' testcases/codegen src/builtin/builtin.ll
```
