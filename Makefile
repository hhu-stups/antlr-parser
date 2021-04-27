FILE=CAN_BUS_tlc.mch
test:
	./gradlew run -Pfile="$(FILE)" -Ptypecheck="false"
build/libs/antlr-parser-0.1.0-SNAPSHOT.jar: src/main/java/de/prob/parser/antlr/*.java
	./gradlew fatJar
testjar: build/libs/antlr-parser-0.1.0-SNAPSHOT.jar
	time java -jar build/libs/antlr-parser-0.1.0-SNAPSHOT.jar $(FILE) false 