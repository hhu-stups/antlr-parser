package de.prob.parser.ast.visitors;

import de.prob.parser.antlr.ScopeException;
import de.prob.parser.antlr.VisitorException;
import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;
import de.prob.parser.ast.nodes.EnumeratedSetDeclarationNode;
import de.prob.parser.ast.nodes.MachineNode;
import de.prob.parser.ast.nodes.MachineReferenceNode;
import de.prob.parser.ast.nodes.Node;
import de.prob.parser.ast.nodes.OperationNode;
import de.prob.parser.ast.nodes.expression.ExprNode;
import de.prob.parser.ast.nodes.expression.IdentifierExprNode;
import de.prob.parser.ast.nodes.expression.LambdaNode;
import de.prob.parser.ast.nodes.expression.LetExpressionNode;
import de.prob.parser.ast.nodes.expression.QuantifiedExpressionNode;
import de.prob.parser.ast.nodes.expression.RecordFieldAccessNode;
import de.prob.parser.ast.nodes.expression.RecordNode;
import de.prob.parser.ast.nodes.expression.SetComprehensionNode;
import de.prob.parser.ast.nodes.expression.StructNode;
import de.prob.parser.ast.nodes.predicate.IdentifierPredicateNode;
import de.prob.parser.ast.nodes.predicate.LetPredicateNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;
import de.prob.parser.ast.nodes.predicate.QuantifiedPredicateNode;
import de.prob.parser.ast.nodes.substitution.AnySubstitutionNode;
import de.prob.parser.ast.nodes.substitution.AssignSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.BecomesElementOfSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.BecomesSuchThatSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.LetSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.OperationCallSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.SkipSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.VarSubstitutionNode;
import de.prob.parser.ast.types.IntegerType;
import de.prob.parser.ast.visitors.generic.ASTVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MachineScopeChecker {
	private final LinkedList<Map<String, DeclarationNode>> scopeTable = new LinkedList<>();
	private final Map<String, OperationNode> operationsInScope = new TreeMap<>();

	private MachineNode machineNode;

	private List<MachineNode> machinesInScope;
	private List<DeclarationNode> setsInScope;
	private List<DeclarationNode> constantsInScope;
	private List<DeclarationNode> variablesInScope;

	private Map<String, DeclarationNode> externalFunctionsAndVariables = new HashMap<>();

	private Map<String, OperationNode> externalOperations = new HashMap<>();

	public MachineScopeChecker(MachineNode machineNode) throws ScopeException {
		this.machineNode = machineNode;
		DeclarationNode socketNode = new DeclarationNode(machineNode.getSourceCodePosition(), "socket", DeclarationNode.Kind.OP_INPUT_PARAMETER, machineNode);
		socketNode.setType(IntegerType.getInstance());
		externalOperations.put("ZMQ_RPC_DESTROY", new OperationNode(machineNode.getSourceCodePosition(), "ZMQ_RPC_DESTROY", new ArrayList<>(), new SkipSubstitutionNode(machineNode.getSourceCodePosition()),
				Collections.singletonList(socketNode)));
		externalFunctionsAndVariables.put("RpcSuccess", new DeclarationNode(machineNode.getSourceCodePosition(), "RpcSuccess", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("ZMQ_RPC_SEND", new DeclarationNode(machineNode.getSourceCodePosition(), "ZMQ_RPC_SEND", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("RpcString", new DeclarationNode(machineNode.getSourceCodePosition(), "RpcString", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("RpcBoolean", new DeclarationNode(machineNode.getSourceCodePosition(), "RpcBoolean", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("RpcArray", new DeclarationNode(machineNode.getSourceCodePosition(), "RpcArray", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("RpcInteger", new DeclarationNode(machineNode.getSourceCodePosition(), "RpcInteger", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("RpcFloat", new DeclarationNode(machineNode.getSourceCodePosition(), "RpcFloat", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("RDIV", new DeclarationNode(machineNode.getSourceCodePosition(), "RDIV", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("RMUL", new DeclarationNode(machineNode.getSourceCodePosition(), "RMUL", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("real", new DeclarationNode(machineNode.getSourceCodePosition(), "real", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("floor", new DeclarationNode(machineNode.getSourceCodePosition(), "floor", DeclarationNode.Kind.CONSTANT, machineNode));
		externalFunctionsAndVariables.put("ZMQ_RPC_INIT", new DeclarationNode(machineNode.getSourceCodePosition(), "ZMQ_RPC_INIT", DeclarationNode.Kind.CONSTANT, machineNode));
		try {
			check();
		} catch (VisitorException e) {
			throw (ScopeException) e.getCause();
		}
	}

	public MachineNode getMachineNode() {
		return this.machineNode;
	}

	public void checkExpression(ExprNode exprNode) {
		FormulaScopeChecker formulaScopeChecker = new FormulaScopeChecker();
		formulaScopeChecker.visitExprNode(exprNode);
	}

	public void checkPredicate(PredicateNode predicateNode) {
		FormulaScopeChecker formulaScopeChecker = new FormulaScopeChecker();
		formulaScopeChecker.visitPredicateNode(predicateNode);
	}

	private void check() {
		FormulaScopeChecker formulaScopeChecker = new FormulaScopeChecker();

		if (machineNode.getProperties() != null) {
			scopeTable.clear();
			createNewScope(getSetsInScope());
			createNewScope(getConstantsInScope());
			formulaScopeChecker.visitPredicateNode(machineNode.getProperties());
		}

		if (machineNode.getValues() != null) {
			scopeTable.clear();
			createNewScope(getSetsInScope());
			createNewScope(getConstantsInScope());
			machineNode.getValues().forEach(formulaScopeChecker::visitSubstitutionNode);
		}

		if (machineNode.getInvariant() != null) {
			scopeTable.clear();
			createNewScope(getSetsInScope());
			createNewScope(getConstantsInScope());
			createNewScope(getVariablesInScope());
			formulaScopeChecker.visitPredicateNode(machineNode.getInvariant());
		}

		if (machineNode.getAssertions() != null) {
			scopeTable.clear();
			createNewScope(getSetsInScope());
			createNewScope(getConstantsInScope());
			createNewScope(getVariablesInScope());
			machineNode.getAssertions().forEach(formulaScopeChecker::visitPredicateNode);
		}

		addOperationsToScope(machineNode, true);
		if (machineNode.getInitialisation() != null) {
			scopeTable.clear();
			createNewScope(getSetsInScope());
			createNewScope(getConstantsInScope());
			createNewScope(getVariablesInScope());
			formulaScopeChecker.visitSubstitutionNode(machineNode.getInitialisation());
		}

		for (OperationNode op : machineNode.getOperations()) {
			createNewScope(getSetsInScope());
			createNewScope(getConstantsInScope());
			createNewScope(getVariablesInScope());
			createNewScope(op.getParams());
			createNewScope(op.getOutputParams());
			formulaScopeChecker.visitSubstitutionNode(op.getSubstitution());
		}
	}

	private void addOperationsToScope(MachineNode mNode, boolean first) {
		// TODO: Implement semantics for PROMOTES
		// A machine can invoke all operations in directly included machines
		// Operations from transitively included machines cannot be invoked directly unless they are promoted by a directly included machine
		if (!first) {
			if(mNode.getPrefix() == null) {
				for (OperationNode op : mNode.getOperations()) {
					this.operationsInScope.put(op.getName(), op);
				}
			} else {
				for (OperationNode op : mNode.getOperations()) {
					this.operationsInScope.put(mNode.getPrefix() + "." + op.getName(), op);
				}
			}
		}
		for (MachineReferenceNode ref : mNode.getMachineReferences()) {
			MachineNode refMachine = ref.getMachineNode();
			if (ref.getType() == MachineReferenceNode.Kind.EXTENDED
					|| ref.getType() == MachineReferenceNode.Kind.INCLUDED
					|| ref.getType() == MachineReferenceNode.Kind.IMPORTED
					|| ref.getType() == MachineReferenceNode.Kind.SEEN) {
				addOperationsToScope(refMachine, false);
			}
		}
	}

	public List<DeclarationNode> getConstantsInScope() {
		if (constantsInScope == null) {
			constantsInScope = getConstantsInScope(getMachinesInScope());
		}
		return constantsInScope;
	}

	public List<DeclarationNode> getConstantsInScope(List<MachineNode> list) {
		List<DeclarationNode> result = new ArrayList<>();
		for (MachineNode machine : list) {
			if(machine.getPrefix() != null && !machineNode.equals(machine)) {
				result.addAll(machine.getConstants().stream()
					.map(decl -> {
						DeclarationNode newNode = new DeclarationNode(decl.getSourceCodePosition(), machine.getPrefix() + "." + decl.getName(), DeclarationNode.Kind.VARIABLE, decl.getSurroundingMachineNode());
						newNode.setType(decl.getType());
						newNode.setParent(decl.getParent());
						return newNode;
					})
					.collect(Collectors.toList()));
			} else {
				result.addAll(machine.getConstants());
			}
		}
		return result;
	}

	public List<DeclarationNode> getVariablesInScope() {
		if (variablesInScope == null) {
			variablesInScope = getVariablesInScope(getMachinesInScope());
		}
		return variablesInScope;
	}

	public List<DeclarationNode> getVariablesInScope(List<MachineNode> list) {
		List<DeclarationNode> result = new ArrayList<>();
		for (int i = list.size() - 1; i >= 0; i--) {
			MachineNode machine = list.get(i);
			if(machine.getPrefix() != null && !machineNode.equals(machine)) {
				List<DeclarationNode> includedRenamedVariables = machine.getVariables().stream()
						.map(decl -> {
							DeclarationNode newNode = new DeclarationNode(decl.getSourceCodePosition(), machine.getPrefix() + "." + decl.getName(), DeclarationNode.Kind.VARIABLE, decl.getSurroundingMachineNode());
							newNode.setParent(decl.getParent());
							return newNode;
						})
						.collect(Collectors.toList());
				result.addAll(includedRenamedVariables);
				machineNode.getIncludedRenamedVariables().addAll(includedRenamedVariables);
			}
			if(machine.getIncludedRenamedVariables() != null) {
				result.addAll(machine.getIncludedRenamedVariables());
				machineNode.getIncludedRenamedVariables().addAll(machine.getIncludedRenamedVariables());
			}
			result.addAll(machine.getVariables());
		}
		return result;
	}

	private List<DeclarationNode> getSetsInScope() {
		if (this.setsInScope == null) {
			setsInScope = getSetsInScope(getMachinesInScope());
		}
		return setsInScope;
	}

	private List<DeclarationNode> getSetsInScope(List<MachineNode> list) {
		List<DeclarationNode> result = new ArrayList<>();
		for (MachineNode machine : list) {
			for (EnumeratedSetDeclarationNode enumSet : machine.getEnumeratedSets()) {
				result.add(enumSet.getSetDeclarationNode());
				result.addAll(enumSet.getElements());
				if(machine.getPrefix() != null && !machineNode.equals(machine)) {
					result.addAll(enumSet.getElements().stream()
							.map(decl -> {
								DeclarationNode newNode = new DeclarationNode(decl.getSourceCodePosition(), machine.getPrefix() + "." + decl.getName(), DeclarationNode.Kind.ENUMERATED_SET_ELEMENT, decl.getSurroundingMachineNode());
								newNode.setType(decl.getType());
								newNode.setParent(decl.getParent());
								return newNode;
							})
							.collect(Collectors.toList()));
				}
			}
			result.addAll(machine.getDeferredSets());
		}
		return result;
	}

	private List<MachineNode> getMachinesInScope() {
		if (this.machinesInScope == null) {
			machinesInScope = new ArrayList<>();
			machinesInScope.add(this.machineNode);
			for (MachineReferenceNode ref : this.machineNode.getMachineReferences()) {
				MachineNode refMachine = ref.getMachineNode();
				machinesInScope.add(refMachine);
				if (ref.getType() == MachineReferenceNode.Kind.EXTENDED
						|| ref.getType() == MachineReferenceNode.Kind.INCLUDED) {
					machinesInScope.addAll(getMachinesInScope(refMachine).stream().filter(m -> !machinesInScope.contains(m)).collect(Collectors.toList()));
				}
			}
		}
		return machinesInScope;

	}

	private List<MachineNode> getMachinesInScope(MachineNode mNode) {
		List<MachineNode> result = new ArrayList<>();
		result.add(mNode);
		for (MachineReferenceNode ref : mNode.getMachineReferences()) {
			MachineNode refMachine = ref.getMachineNode();
			if (ref.getType() == MachineReferenceNode.Kind.EXTENDED
					|| ref.getType() == MachineReferenceNode.Kind.INCLUDED) {
				result.addAll(getMachinesInScope(refMachine).stream().filter(m -> !result.contains(m)).collect(Collectors.toList()));
			}
		}
		return result;
	}

	private void createNewScope(List<DeclarationNode> list) {
		Map<String, DeclarationNode> scope = new TreeMap<>();
		for (DeclarationNode declarationNode : list) {
			scope.put(declarationNode.getName(), declarationNode);
		}
		this.scopeTable.add(scope);
	}

	class FormulaScopeChecker extends ASTVisitor {

		@Override
		public void visitIdentifierExprNode(IdentifierExprNode node) {
			DeclarationNode declarationNode = lookUpIdentifier(node.getName(), node);
			node.setDeclarationNode(declarationNode);
		}

		@Override
		public void visitVarSubstitutionNode(VarSubstitutionNode node) {
			createNewScope(node.getLocalIdentifiers());
			visitSubstitutionNode(node.getBody());
			scopeTable.removeLast();
		}

		@Override
		public void visitSubstitutionIdentifierCallNode(OperationCallSubstitutionNode node) {
			List<String> names = node.getNames();
			String opName = String.join(".", names);
			if (operationsInScope.containsKey(opName)) {
				node.setOperationsNode(operationsInScope.get(opName));
			} else if(externalOperations.containsKey(opName)) {
				node.setOperationsNode(externalOperations.get(opName));
			} else {
				throw new VisitorException(new ScopeException("Unknown operation name: " + opName));
			}
			for (ExprNode arg : node.getArguments()) {
				visitExprNode(arg);
			}
		}

		@Override
		public void visitQuantifiedExpressionNode(QuantifiedExpressionNode node) {
			createNewScope(node.getDeclarationList());
			visitPredicateNode(node.getPredicateNode());
			visitExprNode(node.getExpressionNode());
			scopeTable.removeLast();
		}

		@Override
		public void visitSetComprehensionNode(SetComprehensionNode node) {
			//TODO: variables outside regular scope available
			createNewScope(node.getDeclarationList());
			visitPredicateNode(node.getPredicateNode());
			scopeTable.removeLast();
		}

		@Override
		public void visitLambdaNode(LambdaNode node) {
			//TODO: variables outside regular scope available
			createNewScope(node.getDeclarations());
			visitPredicateNode(node.getPredicate());
			visitExprNode(node.getExpression());
			scopeTable.removeLast();
		}

		@Override
		public void visitIdentifierPredicateNode(IdentifierPredicateNode node) {
			DeclarationNode declarationNode = lookUpIdentifier(node.getName(), node);
			node.setDeclarationNode(declarationNode);
		}

		@Override
		public void visitQuantifiedPredicateNode(QuantifiedPredicateNode node) {
			//TODO: variables outside regular scope available
			createNewScope(node.getDeclarationList());
			visitPredicateNode(node.getPredicateNode());
			scopeTable.removeLast();
		}

		@Override
		public void visitAnySubstitution(AnySubstitutionNode node) {
			createNewScope(node.getParameters());
			visitPredicateNode(node.getWherePredicate());
			visitSubstitutionNode(node.getThenSubstitution());
			scopeTable.removeLast();
		}

		@Override
		public void visitLetSubstitution(LetSubstitutionNode node) {
			createNewScope(node.getLocalIdentifiers());
			visitPredicateNode(node.getPredicate());
			visitSubstitutionNode(node.getBody());
			scopeTable.removeLast();
		}

		@Override
		public void visitLetExpressionNode(LetExpressionNode node) {
			createNewScope(node.getLocalIdentifiers());
			visitPredicateNode(node.getPredicate());
			visitExprNode(node.getExpression());
			scopeTable.removeLast();
		}

		@Override
		public void visitLetPredicateNode(LetPredicateNode node) {
			createNewScope(node.getLocalIdentifiers());
			visitPredicateNode(node.getPredicate());
			visitPredicateNode(node.getPredicate());
			scopeTable.removeLast();
		}

		@Override
		public void visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node) {
			for (ExprNode expr : node.getIdentifiers()) {
				visitExprNode(expr);
			}
			visitExprNode(node.getExpression());
		}

		@Override
		public void visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node) {
			for (ExprNode expr : node.getIdentifiers()) {
				visitExprNode(expr);
			}
			visitPredicateNode(node.getPredicate());
		}

		@Override
		public void visitAssignSubstitutionNode(AssignSubstitutionNode node) {
			for (ExprNode expr : node.getLeftSide()) {
				visitExprNode(expr);
			}
			for (ExprNode expr : node.getRightSide()) {
				visitExprNode(expr);
			}
		}

		@Override
		public void visitStructNode(StructNode node) {
			//Do not visit declared fields
		}

		@Override
		public void visitRecordNode(RecordNode node) {
			//Do not visit declared fields
		}

		@Override
		public void visitRecordFieldAccessNode(RecordFieldAccessNode node) {
			visitExprNode(node.getRecord());
		}
	}

	public DeclarationNode lookUpIdentifier(String name, Node node) {
		if(externalFunctionsAndVariables.containsKey(name)) {
			return externalFunctionsAndVariables.get(name);
		}
		for (int i = scopeTable.size() - 1; i >= 0; i--) {
			Map<String, DeclarationNode> map = scopeTable.get(i);
			if (map.containsKey(name)) {
				DeclarationNode declarationNode = map.get(name);
				return declarationNode;
			}
		}
		throw new VisitorException(new ScopeException("Unknown identifier: " + name));
	}

}
