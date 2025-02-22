package de.prob.parser.ast.nodes;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.ltl.LTLFormula;
import de.prob.parser.ast.nodes.predicate.PredicateNode;
import de.prob.parser.ast.nodes.substitution.SubstitutionNode;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import files.BParser.FormulaContext;
import files.BParser.FormulaPredicateContext;
import files.BParser.FormulaSubstitutionContext;
import files.BParser.FormulaExpressionContext;

public class MachineNode extends Node {

	private String prefix = null;
	private String machineName;
	private List<EnumeratedSetDeclarationNode> setEnumerations = new ArrayList<>();
	private List<DeclarationNode> deferredSets = new ArrayList<>();
	private List<FreetypeDeclarationNode> freetypes = new ArrayList<>();
	private List<DeclarationNode> constants = new ArrayList<>();
	private List<DeclarationNode> variables = new ArrayList<>();
	private List<DeclarationNode> includedRenamedVariables = new ArrayList<>();
	private List<MachineReferenceNode> machineReferences = new ArrayList<>();
	private List<OperationReferenceNode> operationReferences = new ArrayList<>();
	private PredicateNode properties;
	private PredicateNode invariant;
	private List<PredicateNode> assertions;
	private SubstitutionNode initialisation;
	private List<OperationNode> operations = new ArrayList<>();
	private List<SubstitutionNode> values = new ArrayList<>();
	private List<DefinitionNode> definitions = new ArrayList<>();

	private List<LTLFormula> ltlFormulas = new ArrayList<>();

	public String getName() {
		return this.machineName;
	}

	public List<DeclarationNode> getVariables() {
		return variables;
	}

	public void addVariables(List<DeclarationNode> variables) {
		this.variables.addAll(variables);
		this.variables.forEach(var -> var.setParent(this));
	}

	public List<DeclarationNode> getIncludedRenamedVariables() {
		return includedRenamedVariables;
	}

	public MachineNode(SourceCodePosition sourceCodePosition) {
		super(sourceCodePosition);
	}

	public List<DeclarationNode> getConstants() {
		return constants;
	}

	public void addConstants(List<DeclarationNode> constants) {
		this.constants.addAll(constants);
		this.constants.forEach(constant -> constant.setParent(this));
	}

	public void addLTLFormula(LTLFormula ltlFormula) {
		this.ltlFormulas.add(ltlFormula);
		ltlFormula.setParent(this);
	}

	public SubstitutionNode getInitialisation() {
		return initialisation;
	}

	public void setInitialisation(SubstitutionNode initialisation) {
		this.initialisation = initialisation;
		this.initialisation.setParent(this);
	}

	public List<OperationNode> getOperations() {
		return operations;
	}

	public List<DefinitionNode> getDefinitions() {
		return this.definitions;
	}

	public void setOperations(List<OperationNode> operations) {
		this.operations = operations;
		this.operations.forEach(op -> op.setParent(this));
	}

	public PredicateNode getInvariant() {
		return invariant;
	}

	public void setInvariant(PredicateNode invariant) {
		this.invariant = invariant;
		if(this.getInvariant() != null) {
			this.invariant.setParent(this);
		}
	}

	public PredicateNode getProperties() {
		return properties;
	}

	public void setProperties(PredicateNode properties) {
		this.properties = properties;
		this.properties.setParent(this);
	}

	public List<PredicateNode> getAssertions() {
		return assertions;
	}

	public void setAssertions(List<PredicateNode> assertions) {
		this.assertions = assertions;
		this.assertions.forEach(assertion -> assertion.setParent(this));
	}

	public void addSetEnumeration(EnumeratedSetDeclarationNode setEnumeration) {
		this.setEnumerations.add(setEnumeration);
		setEnumeration.setParent(this);
	}

	public List<EnumeratedSetDeclarationNode> getEnumeratedSets() {
		return new ArrayList<>(this.setEnumerations);
	}

	public void addDeferredSet(DeclarationNode setDeclNode) {
		this.deferredSets.add(setDeclNode);
		setDeclNode.setParent(this);
	}

	public List<DeclarationNode> getDeferredSets() {
		return new ArrayList<>(this.deferredSets);
	}

	public void addFreetype(FreetypeDeclarationNode freetypeNode) {
		this.freetypes.add(freetypeNode);
		freetypeNode.setParent(this);
	}

	public List<FreetypeDeclarationNode> getFreetypes() {
		return new ArrayList<>(this.freetypes);
	}

	public List<LTLFormula> getLTLFormulas() {
		return new ArrayList<>(this.ltlFormulas);
	}

	public void addOperation(OperationNode operationNode) {
		this.operations.add(operationNode);
		operationNode.setParent(this);
	}

	public void addMachineReferenceNode(MachineReferenceNode machineReferenceNode) {
		machineReferences.add(machineReferenceNode);
		machineReferenceNode.setParent(this);
	}

	public List<MachineReferenceNode> getMachineReferences() {
		return machineReferences;
	}

	public void addOperationReferenceNode(OperationReferenceNode operationReferenceNode) {
		operationReferences.add(operationReferenceNode);
		operationReferenceNode.setParent(this);
	}

	public List<OperationReferenceNode> getOperationReferences() {
		return operationReferences;
	}

	public void addValues(SubstitutionNode substitution) {
		values.add(substitution);
		substitution.setParent(this);
	}

	public List<SubstitutionNode> getValues() {
		return values;
	}

	public void setName(String name) {
		this.machineName = name;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public void removeChild(Node child) {

	}

	@Override
	public String toString() {
		if (prefix != null) {
			return this.prefix + "." + this.machineName;
		}
		return this.machineName;
	}

	public void addDefinition(DefinitionNode definitionNode) {
		this.definitions.add(definitionNode);
	}

}
