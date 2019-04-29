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

	String prepareToString(String selectIf) {
		StringBuilder sb = new StringBuilder();
		sb.append(selectIf).append(" ").append(condition).append(" THEN ").append(thenPred);
		sb.append(" ELSE ").append(elsePred);
		sb.append(" END");
		return sb.toString();
	}

}
