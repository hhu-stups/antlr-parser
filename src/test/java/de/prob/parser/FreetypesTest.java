package de.prob.parser;

import de.prob.parser.antlr.Antlr4BParser;
import de.prob.parser.antlr.BProject;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class FreetypesTest {

	@Test
	public void testFreetype() throws Exception {
		String machine = "MACHINE FT\n";
		machine += "FREETYPES A = a, b(BOOL)\n";
		machine += "CONSTANTS x, y\n";
		machine += "PROPERTIES x=a & y=b(TRUE)\n";
		machine += "END";
		BProject p = Antlr4BParser.createBProjectFromMachineStrings(machine);
		assertNotNull(p);
	}

	@Test
	public void testFreetypeRecursive() throws Exception {
		String machine = "MACHINE FT\n";
		machine += "FREETYPES A = a, b(A)\n";
		machine += "CONSTANTS x, y\n";
		machine += "PROPERTIES x=a & y=b(a)\n";
		machine += "END";
		BProject p = Antlr4BParser.createBProjectFromMachineStrings(machine);
		assertNotNull(p);
	}
}
