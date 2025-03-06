package de.prob.parser;

import de.prob.parser.antlr.Antlr4BParser;
import de.prob.parser.antlr.BProject;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TypingTest {

	@Test
	public void testDom() throws Exception {
		String machine = "MACHINE X\n";
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
		String machine = "MACHINE X\n";
		machine += "CONSTANTS F\n";
		machine += "PROPERTIES F : INTEGER +-> BOOL\n";
		machine += "VARIABLES x\n";
		machine += "INVARIANT x : struct(a: dom(F))\n";
		machine += "END";
		BProject p = Antlr4BParser.createBProjectFromMachineStrings(machine);
		assertNotNull(p);
	}
}
