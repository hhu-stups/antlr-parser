package de.prob.parser.ast.nodes.predicate;

import de.prob.parser.ast.SourceCodePosition;

public class IfPredicateNode extends PredicateNode {

	protected PredicateNode condition;
	protected PredicateNode thenPred;
	protected PredicateNode elsePred;


	public IfPredicateNode(SourceCodePosition sourceCodePosition, PredicateNode condition,
						   PredicateNode thenPred, PredicateNode elsePred) {
		super(sourceCodePosition);
		this.condition = condition;
		this.thenPred = thenPred;
		this.elsePred = elsePred;
		this.condition.setParent(this);
		this.thenPred.setParent(this);
		this.elsePred.setParent(this);
	}

	public PredicateNode getCondition() {
		return this.condition;
	}

	public PredicateNode getThenPredicate() {
		return this.thenPred;
	}

	public PredicateNode getElsePredicate() {
		return this.elsePred;
	}

	public void setCondition(PredicateNode condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		return "IF " + condition + " THEN " + thenPred + " ELSE " + elsePred + " END";
	}
}
