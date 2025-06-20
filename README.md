# Incremental Compilation: An Advanced Fake Mx* Compiler

## Goal

- [x] Linker
- [x] Function-level detect and recompile
- [x] Expression-level replacement
- [ ] Symbol renaming detect
- - [x] function rename
- - [ ] variable rename
- - [ ] class rename
- [ ] String literal replacement
- [ ] ~~Remote~~
- [x] Multi-Threading (a big lock)

## Cache structure

`${file_name}.ast.cache` is an array of cache blocks which is of exactly 256 bytes.

```
|--type---|-----hash-of-the-sub-ast-----|--parent-index--|---additional-info---|
|    4    |            64               |       4        |          56         |
```

The parent index constructs the array into a tree, and we use a trimmed `cAST` and its derivations
to store only the structure, the additional information (e.g. function name), and every child of an AST node.

`${function_name}` is a data cache corresponding to a function that is the latest compiled. 

Note that a function has a signature and a realization. The first line is the hash of the whole function, 
while the second is the hash of the function body. It is designed so that we can compare two functions by hash and 
do function renaming, which means that, when the user renames a function but does not modify its content, we 
shall use the cache.

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
