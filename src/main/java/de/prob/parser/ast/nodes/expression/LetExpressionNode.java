package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

import java.util.List;

public class LetExpressionNode extends ExprNode {

	private List<DeclarationNode> localVariables;
	private PredicateNode predicate;
	private ExprNode expression;

	public LetExpressionNode(SourceCodePosition sourceCodePosition, List<DeclarationNode> localIdentifiers,
                             PredicateNode predicate, ExprNode expression) {
		super(sourceCodePosition);
		this.localVariables = localIdentifiers;
		this.predicate = predicate;
		this.expression = expression;
		this.localVariables.forEach(node -> node.setParent(this));
		this.predicate.setParent(this);
		this.expression.setParent(this);
	}

	public List<DeclarationNode> getLocalIdentifiers() {
		return localVariables;
	}

	public ExprNode getExpression() {
		return expression;
	}

	public PredicateNode getPredicate() {
		return predicate;
	}

	public void setExpression(ExprNode expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return "LET " + localVariables + " BE " + predicate + " IN " + expression + " END";
	}

}
