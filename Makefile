JAVA_SRC = $(shell find src -name '*.java')

ANTLR_JAR = /ulib/antlr-4.13.1-complete.jar
LOCAL_ANTLR_JAR = /usr/local/bin/antlr-4.13.2-complete.jar
BUILTIN_IR = src/builtin/builtin.ll
BUILTIN_ASM = src/builtin/builtin.s

MAIN_CLASS = src.Main

.PHONY: build
build:
	javac -d bin $(JAVA_SRC) -cp $(ANTLR_JAR)

.PHONY: run
run:
	java -cp $(ANTLR_JAR):bin $(MAIN_CLASS)

.PHONY: test_asm
test_asm:
	@javac -d bin $(JAVA_SRC) -cp $(LOCAL_ANTLR_JAR)
	@$(PWD)/test_asm_reimu.bash 'java -cp $(LOCAL_ANTLR_JAR):bin $(MAIN_CLASS) -S' testcases/codegen/test/t1.mx $(BUILTIN_ASM)

.PHONY: test_ir
test_ir:
	@javac -d bin $(JAVA_SRC) -cp $(LOCAL_ANTLR_JAR)
	@$(PWD)/testcases/codegen/scripts/test_llvm_ir.bash 'java -cp $(LOCAL_ANTLR_JAR):bin $(MAIN_CLASS) -S' testcases/codegen/test/t2.mx $(BUILTIN_IR)

.PHONY: test
test:
ifeq ($(findstring i,$(MAKEFLAGS)),i)
test: test_ir
endif
ifeq ($(findstring a,$(MAKEFLAGS)),a)
test: test_asm
endif

.PHONY: ir
ir:
	@javac -d bin $(JAVA_SRC) -cp $(LOCAL_ANTLR_JAR)
	@$(PWD)/run_llvm_ir.bash 'java -cp $(LOCAL_ANTLR_JAR):bin $(MAIN_CLASS) -S' testcases/multi/t2/main.mx $(BUILTIN_IR)


.PHONY: clean
clean:
	find bin -name '*.class' -or -name '*.jar' | xargs rm -f
