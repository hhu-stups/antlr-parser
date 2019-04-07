package de.prob.parser.ast.nodes.predicate;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;
import de.prob.parser.ast.nodes.expression.IdentifierExprNode;

public class IdentifierPredicateNode extends PredicateNode {

	private DeclarationNode declarationNode;
	private IdentifierExprNode identifierNode;

	public IdentifierPredicateNode(SourceCodePosition sourceCodePosition, IdentifierExprNode identifierNode) {
		super(sourceCodePosition);
		this.identifierNode = identifierNode;
	}

	public void setDeclarationNode(DeclarationNode declarationNode) {
		this.declarationNode = declarationNode;
	}

	public DeclarationNode getDeclarationNode() {
		return declarationNode;
	}

	public IdentifierExprNode getIdentifierNode() {
		return identifierNode;
	}

	public String getName() {
		return this.identifierNode.getName();
	}

	@Override
	public String toString() {
		return this.identifierNode.getName();
	}
}
