package de.prob.parser.ast.nodes.substitution;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.expression.ExprNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

public class WhileSubstitutionNode extends SubstitutionNode {
	private final PredicateNode condition;
	private final SubstitutionNode body;
	private final PredicateNode invariant;
	private final ExprNode variant;

	public WhileSubstitutionNode(SourceCodePosition sourceCodePosition, PredicateNode condition, SubstitutionNode body,
			PredicateNode invariant, ExprNode variant) {
		super(sourceCodePosition);
		this.condition = condition;
		this.body = body;
		this.invariant = invariant;
		this.variant = variant;
		this.condition.setParent(this);
		this.body.setParent(this);
		this.invariant.setParent(this);
		this.variant.setParent(this);
	}

	public ExprNode getVariant() {
		return variant;
	}

	public PredicateNode getCondition() {
		return condition;
	}

	public SubstitutionNode getBody() {
		return body;
	}

	public PredicateNode getInvariant() {
		return invariant;
	}

	@Override
	public String toString() {
		return "WHILE " + condition + " DO " + body + " INVARIANT " + invariant + " VARIANT " + variant + " END";
	}
}
