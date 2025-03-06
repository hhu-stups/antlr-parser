package de.prob.parser;

import de.prob.parser.antlr.Antlr4BParser;
import de.prob.parser.antlr.BProject;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TypingTest {

	@Test
	public void testDom() throws Exception {
		String machine = "MACHINE M\n";
		machine += "CONSTANTS F\n";
		machine += "PROPERTIES F : INTEGER +-> BOOL\n";
		machine += "VARIABLES x\n";
		machine += "INVARIANT x : dom(F)\n";
		machine += "END";
		BProject p = Antlr4BParser.createBProjectFromMachineStrings(machine);
		assertNotNull(p);
	}

	@Test
	public void testDomInStruct() throws Exception {
		String machine = "MACHINE M\n";
		machine += "CONSTANTS F\n";
		machine += "PROPERTIES F : INTEGER +-> BOOL\n";
		machine += "VARIABLES x\n";
		machine += "INVARIANT x : struct(a: dom(F))\n";
		machine += "END";
		BProject p = Antlr4BParser.createBProjectFromMachineStrings(machine);
		assertNotNull(p);
	}

	@Test
	public void testFreetype() throws Exception {
		String machine = "MACHINE M\n";
		machine += "FREETYPES A = a, b(BOOL)\n";
		machine += "CONSTANTS x, y\n";
		machine += "PROPERTIES x=a & y=b(TRUE)\n";
		machine += "END";
		BProject p = Antlr4BParser.createBProjectFromMachineStrings(machine);
		assertNotNull(p);
	}

	@Test
	public void testFreetypeRecursive() throws Exception {
		String machine = "MACHINE M\n";
		machine += "FREETYPES A = a, b(A)\n";
		machine += "CONSTANTS x, y\n";
		machine += "PROPERTIES x=a & y=b(a)\n";
		machine += "END";
		BProject p = Antlr4BParser.createBProjectFromMachineStrings(machine);
		assertNotNull(p);
	}

	@Test
	public void testFieldAccessForLocalVar() throws Exception {
		String machine = "MACHINE M\n";
		machine += "VARIABLES x\n";
		machine += "INVARIANT x : struct(a: INTEGER)\n";
		machine += "OPERATIONS op = SELECT LET p BE p = x'a IN 1=1 END THEN skip END\n";
		machine += "END";
		BProject p = Antlr4BParser.createBProjectFromMachineStrings(machine);
		assertNotNull(p);
	}

	@Test
	public void testFieldAccessForLocalVarInFreetype() throws Exception {
		String machine = "MACHINE M\n";
		machine += "FREETYPES F = s(struct(a: INTEGER))\n";
		machine += "VARIABLES x\n";
		machine += "INVARIANT x : F\n";
		machine += "OPERATIONS op = SELECT LET p BE p = s~(x)'a IN 1=1 END THEN skip END\n";
		machine += "END";
		BProject p = Antlr4BParser.createBProjectFromMachineStrings(machine);
		assertNotNull(p);
	}
}
