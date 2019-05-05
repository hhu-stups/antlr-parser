package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;

public class IdentifierExprNode extends ExprNode {

	final String name;
	private DeclarationNode declarationNode;
	private boolean isPrimed;

	public IdentifierExprNode(SourceCodePosition sourceCodePosition, String name, boolean isPrimed) {
		super(sourceCodePosition);
		this.name = name;
		this.isPrimed = isPrimed;
	}

	public void setDeclarationNode(DeclarationNode declarationNode) {
		this.declarationNode = declarationNode;
	}

	public DeclarationNode getDeclarationNode() {
		return declarationNode;
	}

	public String getName() {
		return this.name;
	}

	public boolean isPrimed() {
		return isPrimed;
	}

	@Override
	public String toString() {
		return name;
	}

}
