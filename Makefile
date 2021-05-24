FILE=CAN_BUS_tlc.mch
FILE=rule_medium500.mch
ANTLR_JAR=build/libs/antlr-parser-all-0.1.0-SNAPSHOT.jar

test:
	./gradlew run -Pfile="$(FILE)" -Ptypecheck="false"
$(ANTLR_JAR): src/main/java/de/prob/parser/antlr/*.java
	./gradlew fatJar
testjar: $(ANTLR_JAR)
	time java -jar $(ANTLR_JAR) $(FILE) false

DFILE=~/git_root/prob_examples/public_examples/B/Benchmarks/scheduler
#DFILE=~/git_root/prob_examples/public_examples/B/Benchmarks/Cruise_finite1
DFILE=~/git_root/prob_examples/public_examples/B/Benchmarks/phonebook7
#DFILE=~/git_root/prob_examples/public_examples/B/Benchmarks/CSM
DFILE=~/git_root/prob_examples/public_examples/B/Benchmarks/CarlaTravelAgencyErr
DFILE=~/git_root/prob_examples/public_examples/B/Benchmarks/NatRangeLaws
DFILE=~/git_root/prob_examples/public_examples/B/Benchmarks/RouteIsSeq
PBFILE=antlr.prob
diff: $(ANTLR_JAR)
	time java -jar $(ANTLR_JAR) $(DFILE).mch false >$(PBFILE)
	time java -jar $(PHOME)/lib/probcliparser.jar $(DFILE).mch -prolog -time
	probcli $(PBFILE) -pp_with_name PPDIFF antlr_pp.mch
	probcli $(DFILE).mch -pp_with_name PPDIFF sable_pp.mch
	#diff $(PBFILE) $(DFILE).prob
	diff antlr_pp.mch sable_pp.mch

PHOME=~/git_root/prob_prolog/
testoriginal:
	time java -jar $(PHOME)/lib/probcliparser.jar $(FILE) -prolog -time

testdef: $(ANTLR_JAR)
	# time java -jar $(PHOME)/lib/probcliparser.jar tickets/SimpleDefTest.mch -prolog -time
	java -jar $(ANTLR_JAR) tickets/SimpleDefTest.mch false
