package de.prob.parser.ast.nodes.substitution;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.OperationNode;
import de.prob.parser.ast.nodes.expression.ExprNode;

import java.util.ArrayList;
import java.util.List;

public class OperationCallSubstitutionNode extends SubstitutionNode {

	private List<String> names;
	private List<ExprNode> arguments;
	private OperationNode operationNode;
	private List<ExprNode> assignedVariables;

	public OperationCallSubstitutionNode(SourceCodePosition sourceCodePosition, List<String> names,
			List<ExprNode> arguments) {
		this(sourceCodePosition, names, arguments, new ArrayList<>());
	}

	public OperationCallSubstitutionNode(SourceCodePosition sourceCodePosition, List<String> names,
			List<ExprNode> arguments, List<ExprNode> assignedVariables) {
		super(sourceCodePosition);
		this.names = names;
		this.arguments = arguments;
		this.assignedVariables = assignedVariables;
		this.arguments.forEach(arg -> arg.setParent(this));
		this.assignedVariables.forEach(var -> var.setParent(this));
	}

	public List<String> getNames() {
		return names;
	}

	public List<ExprNode> getArguments() {
		return this.arguments;
	}

	public void setOperationsNode(OperationNode operationNode) {
		this.operationNode = operationNode;
	}

	public OperationNode getOperationNode() {
		return this.operationNode;
	}

	public List<ExprNode> getAssignedVariables() {
		return assignedVariables;
	}

	@Override
	public String toString() {
		return names + "(" + arguments + ")";
	}
}
