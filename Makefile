FILE=CAN_BUS_tlc.mch
FILE=rule_medium500.mch
test:
	./gradlew run -Pfile="$(FILE)" -Ptypecheck="false"
build/libs/antlr-parser-all-0.1.0-SNAPSHOT.jar: src/main/java/de/prob/parser/antlr/*.java
	./gradlew fatJar
testjar: build/libs/antlr-parser-all-0.1.0-SNAPSHOT.jar
	time java -jar build/libs/antlr-parser-all-0.1.0-SNAPSHOT.jar $(FILE) false

PHOME=~/git_root/prob_prolog/
testoriginal:
	time java -jar $(PHOME)/lib/probcliparser.jar $(FILE) -prolog -time
