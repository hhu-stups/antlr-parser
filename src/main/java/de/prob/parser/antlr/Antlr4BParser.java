package de.prob.parser.antlr;

import de.prob.parser.ast.nodes.MachineNode;
import de.prob.parser.ast.nodes.MachineReferenceNode;
import de.prob.parser.ast.visitors.MachineScopeChecker;
import de.prob.parser.ast.visitors.TypeChecker;
import de.prob.parser.ast.visitors.TypeErrorException;
import de.prob.parser.util.Utils;
import files.BLexer;
import files.BParser;
import files.BParser.StartContext;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Antlr4BParser {

	public static MachineNode createSemanticAST(String input) throws TypeErrorException, ScopeException {
		StartContext tree = parse(input);
		MachineNode machineNode = MachineASTCreator.createMachineAST(tree);
		new MachineScopeChecker(machineNode);
		new TypeChecker(machineNode);
		return machineNode;
	}

	public static BProject createBProject(List<MachineNode> machineNodeList) throws TypeErrorException, ScopeException {
		return createBProject(machineNodeList, true, true);
	}

	public static BProject createBProject(List<MachineNode> machineNodeList, boolean typecheck, boolean scopecheck) throws TypeErrorException, ScopeException {
		// determine machine order

		sortMachineNodes(machineNodeList);
		for (int i = machineNodeList.size() - 1; i >= 0; i--) {
			MachineNode machineNode = machineNodeList.get(i);
		    if(scopecheck) {
			    new MachineScopeChecker(machineNode);
			}
		}
		if(typecheck) {
			for (int i = machineNodeList.size() - 1; i >= 0; i--) {
				MachineNode machineNode = machineNodeList.get(i);
				new TypeChecker(machineNode);
			}
		}
		return new BProject(machineNodeList);
	}

	public static BProject createBProjectFromMachineStrings(String main, String... machines)
			throws TypeErrorException, ScopeException {
		List<MachineNode> parsedmachines = parseMachines(main, machines);
		return createBProject(parsedmachines);
	}

	protected static void checkMachineName(File file, String name) {
		if(!file.exists()) {
			throw new RuntimeException(String.format("Machine %s must have the same name as its file", name));
		}
		String path = file.getName().replaceAll(".mch", "");
		if(!path.equals(name)) {
			throw new RuntimeException(String.format("Machine %s must have the same name as its file", name));
		}
	}

	public static BProject createBProjectFromMainMachineFile(File mainBFile) throws TypeErrorException, ScopeException, IOException {
		return createBProjectFromMainMachineFile(mainBFile, true, true);
	}

	public static BProject createBProjectFromMainMachineFile(File mainBFile, boolean typecheck, boolean scopecheck) throws IOException, TypeErrorException, ScopeException {
		final File parentFolder = mainBFile.getParentFile();
		final List<MachineNode> machines = new ArrayList<>();
		final StartContext mainMachineCST = parse(mainBFile);
		final MachineNode main = MachineASTCreator.createMachineAST(mainMachineCST);
		checkMachineName(mainBFile, main.getName());

		machines.add(main);
		final Set<String> parsedMachines = new HashSet<>();
		parsedMachines.add(main.getName());
		final List<MachineReferenceNode> todo = new ArrayList<>();
		todo.addAll(main.getMachineReferences());
		while (!todo.isEmpty()) {
			final MachineReferenceNode next = todo.iterator().next();
			todo.remove(next);
			final String name = next.getMachineName();
			if (!parsedMachines.contains(name)) {
				final File file = getFile(parentFolder, name);
				checkMachineName(file, name);
				final StartContext cst = parse(file);
				final MachineNode ast = MachineASTCreator.createMachineAST(cst);
				ast.setPrefix(next.getPrefix());
				machines.add(ast);
				for (MachineReferenceNode machineReferenceNode : ast.getMachineReferences()) {
					final String refName = machineReferenceNode.getMachineName();
					if (!parsedMachines.contains(refName)) {
						todo.add(machineReferenceNode);
					}
				}
			}
		}
		return createBProject(machines, typecheck, scopecheck);
	}

	protected static File getFile(File parentFolder, String name) {
		// TODO try different file name extensions
		return new File(parentFolder, name + ".mch");
	}

	public static List<MachineNode> parseMachines(String input, String... machines) {
		List<MachineNode> machineNodeList = new ArrayList<>();
		StartContext tree = parse(input);
		MachineNode main = MachineASTCreator.createMachineAST(tree);
		machineNodeList.add(main);
		for (String string : machines) {
			StartContext tree2 = parse(string);
			MachineNode mNode = MachineASTCreator.createMachineAST(tree2);
			machineNodeList.add(mNode);
		}
		return machineNodeList;
	}

	protected static void sortMachineNodes(List<MachineNode> machineNodeList) {
		final Map<String, MachineNode> machineNodeMap = new HashMap<>();
		for (MachineNode machineNode : machineNodeList) {
			machineNodeMap.put(machineNode.toString(), machineNode);
		}
		Map<String, Set<String>> dependencies = new HashMap<>();
		determineMachineDependencies(machineNodeList.get(0), machineNodeMap, dependencies, new ArrayList<>());
		List<String> machineNameList = Utils.sortByTopologicalOrder(dependencies);
		machineNodeList.clear();
		for (String machineName : machineNameList) {
			machineNodeList.add(machineNodeMap.get(machineName));
		}
	}

	protected static void determineMachineDependencies(final MachineNode machineNode,
			final Map<String, MachineNode> machineNodes, final Map<String, Set<String>> dependencies,
			final List<String> ancestors) {
		final String name = machineNode.toString();
		ancestors.add(name);

		final Set<String> set = new HashSet<>();
		for (MachineReferenceNode machineReferenceNode : machineNode.getMachineReferences()) {
			final String refName = machineReferenceNode.toString();
			if (ancestors.contains(refName)) {
				throw new RuntimeException("Cycle detected");
			}
			set.add(refName);
			final MachineNode refMachineNode = machineNodes.get(refName);
			machineReferenceNode.setMachineNode(refMachineNode);
			determineMachineDependencies(refMachineNode, machineNodes, dependencies, new ArrayList<>(ancestors));
			set.addAll(dependencies.get(refName));
		}
		dependencies.put(name, set);

	}

	public static StartContext parse(File bFile) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(bFile);
		CharStream charStream = CharStreams.fromStream(fileInputStream);
		return parse(charStream);
	}

	public static StartContext parse(String bMachineString) {
		CodePointCharStream charStream = CharStreams.fromString(bMachineString);
		return parse(charStream);
	}

	public static StartContext parse(final CharStream charStream) {
		BLexer lexer = new BLexer(charStream);
		// MyLexer myLexer = new MyLexer(fromString);

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// BLexer.rulesGrammar = true;
		// create a parser that feeds off the tokens buffer

		BParser parser = new BParser(tokens);
		// RulesGrammar parser = new RulesGrammar(tokens);

		// parser.addErrorListener(new MyErrorListener());
		// parser.removeErrorListeners();
		parser.addErrorListener(new DiagnosticErrorListener());
		MyErrorListener myErrorListener = new MyErrorListener();
		parser.addErrorListener(myErrorListener);
		StartContext tree = null;

		tree = parser.start();

		// begin parsing at start rule
		// if (myErrorListener.exception != null) {
		// throw new RuntimeException(myErrorListener.exception);
		// }

		// System.out.println(tree.toStringTree(parser)); // print LISP-style
		// tree

		// PragmaListener pragmaListener = new PragmaListener(tokens);
		// ParseTreeWalker walker = new ParseTreeWalker();
		// walker.walk(pragmaListener, tree);

		// MyTreeListener listener = new MyTreeListener();
		// ParseTreeWalker walker2 = new ParseTreeWalker();
		// walker.walk(listener, tree);
		// System.out.println("-------------");

		return tree;
	}

	public static BParser.ExpressionContext parseExpression(final CharStream charStream) {
		BLexer lexer = new BLexer(charStream);
		// MyLexer myLexer = new MyLexer(fromString);

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// BLexer.rulesGrammar = true;
		// create a parser that feeds off the tokens buffer

		BParser parser = new BParser(tokens);
		// RulesGrammar parser = new RulesGrammar(tokens);

		// parser.addErrorListener(new MyErrorListener());
		// parser.removeErrorListeners();
		parser.addErrorListener(new DiagnosticErrorListener());
		MyErrorListener myErrorListener = new MyErrorListener();
		parser.addErrorListener(myErrorListener);
		BParser.ExpressionContext tree = null;

		tree = parser.expression();
		return tree;
	}

	public static void main(String[] args) throws TypeErrorException, ScopeException, IOException, URISyntaxException {
		if(args.length != 1 && args.length != 2) {
			System.out.println("Arguments for ANTLR B Parser is wrong");
			System.out.println("Use java -jar antlr-parser-VERSION.jar FILE [TypeCheck]");
			System.out.println(" where FILE is the B file to parse and TypeCheck is true or false");
			return;
		}

		boolean typecheck = args.length == 1 || Boolean.parseBoolean(args[1]);
		boolean scopecheck =  (args.length<=2) ? typecheck : Boolean.parseBoolean(args[2]);
		// TODO: add options similar to SableCC parser, notably -prolog, or automatically generating .prob file
		
		Path filePath = Paths.get(args[0]);

		final long start = System.currentTimeMillis();
		BProject project = createBProjectFromMainMachineFile(filePath.toFile(), typecheck, scopecheck);
		final long mid = System.currentTimeMillis();
		PrologASTPrinter astPrinter = new PrologASTPrinter();
		String prologAST = astPrinter.visitMachineNode(project.getMainMachine());
		final long end = System.currentTimeMillis();
		System.out.println("% Generated AST for machine: " + project.getMainMachine().getName() + " (Parsing: " + (mid - start) + " ms, AST: " + (end - start) + " ms)");
		System.out.println("parser_version('ANTLR-0.1').");
		System.out.println("classical_b('"+ project.getMainMachine().getName() + "',['" + filePath + "'])."); // TO DO: insert real machine name
		System.out.println(prologAST + ".");
		
        System.out.println("% Used memory : " + 
                   (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/ 1000 + " KB");
        System.out.println("% Total memory: " + Runtime.getRuntime().totalMemory() / 1000 + " KB");
	}

}
