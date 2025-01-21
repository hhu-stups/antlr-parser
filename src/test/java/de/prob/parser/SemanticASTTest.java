package de.prob.parser;

import java.io.File;
import java.net.URL;

import de.prob.parser.antlr.Antlr4BParser;
import de.prob.parser.antlr.BProject;
import de.prob.parser.ast.nodes.MachineNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorWithExprArgsNode;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SemanticASTTest {
	@Test
	public void testRecords() throws Exception {
		String machine = "MACHINE test2\n";
		machine += "CONSTANTS k\n";
		machine += "PROPERTIES k = rec(a:1, b:TRUE) \n";
		machine += "END";
		check(machine);
		// TODO add class for records and structs
	}


	@Test
	public void testWhileLoop() throws Exception {
		String machine = "MACHINE test2\n";
		machine += "OPERATIONS \n";
		machine += "foo = SELECT 1=1 THEN WHILE 1=1 DO skip INVARIANT 1=1 VARIANT 3 END END\n";
		machine += "END";
		check(machine);
	}

	@Test
	public void testEmptySet() throws Exception {
		String machine = "MACHINE Fin1Test\n" +
				"SETS\n" +
				" ID={aa,bb}\n" +
				"/* DEFINITIONS\n" +
				"  x == (1=1 & y); */\n" +
				"VARIABLES xx\n" +
				"INVARIANT\n" +
				" {} : FIN(ID) & {} /: FIN1(ID) & xx:ID\n" +
				"INITIALISATION xx:=aa\n" +
				"END\n";
		check(machine);
		MachineNode node = Antlr4BParser.createBProjectFromMachineStrings(machine).getMainMachine();
		PredicateOperatorWithExprArgsNode predicate = (PredicateOperatorWithExprArgsNode) ((PredicateOperatorNode) node.getInvariant()).getPredicateArguments().get(0);
		assertEquals(predicate.getExpressionNodes().get(0).getType().toString(), "POW(ID)");
		assertEquals(predicate.getExpressionNodes().get(1).getType().toString(), "POW(POW(ID))");
	}

	@Test
	public void testRelLaws() throws Exception {
		checkFile("RelLaws.mch");
		Antlr4BParser.createBProjectFromMainMachineFile(new File("src/test/resources/de/prob/parser/RelLaws.mch"));
	}

	@Test
	public void testExplicitComputations() throws Exception {
		checkFile("ExplicitComputations.mch");
	}

	@Test
	public void testCore() throws Exception {
		checkFile("Core.mch");
	}

	@Test
	public void testCompositionEmpty() throws Exception {
		String machine = "MACHINE CompositionEmpty\n" +
				"VARIABLES\n" +
				"  ff\n" +
				"INVARIANT\n" +
				"  ff : POW(INTEGER * INTEGER)\n" +
				"  & ({}; ff) = {}\n" +
				"\n" +
				"INITIALISATION\n" +
				"    BEGIN\n" +
				"      ff := {(1|->2)}\n" +
				"    END\n" +
				"OPERATIONS\n" +
				"  continue = skip\n" +
				"END\n";
		check(machine);
	}
	
	private BProject check(String main, String... others) throws Exception {
		return Antlr4BParser.createBProjectFromMachineStrings(main, others);
	}
	
	private BProject checkFile(String relativePath) throws Exception {
		URL machineUrl = SemanticASTTest.class.getResource(relativePath);
		Assert.assertNotNull("Machine file not found in test resources", machineUrl);
		File machineFile = new File(machineUrl.toURI());
		return Antlr4BParser.createBProjectFromMainMachineFile(machineFile);
	}
}
