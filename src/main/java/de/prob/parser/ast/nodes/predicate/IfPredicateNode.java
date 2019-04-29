package de.prob.parser.ast.nodes.predicate;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.expression.ExprNode;

import java.util.List;

public class IfPredicateNode extends ExprNode {

	protected List<PredicateNode> conditions;
	protected List<PredicateNode> predicates;
	protected PredicateNode elsePredicate;


	public IfPredicateNode(SourceCodePosition sourceCodePosition, List<PredicateNode> conditions,
                           List<PredicateNode> predicates, PredicateNode elsePredicate) {
		super(sourceCodePosition);
		this.conditions = conditions;
		this.predicates = predicates;
		this.elsePredicate = elsePredicate;
		this.conditions.forEach(cond -> cond.setParent(this));
		this.predicates.forEach(sub -> sub.setParent(this));
		if(elsePredicate != null) {
			this.elsePredicate.setParent(this);
		}
	}

	public List<PredicateNode> getPredicates() {
		return this.predicates;
	}

	public PredicateNode getElsePredicate() {
		return this.elsePredicate;
	}

	public List<PredicateNode> getConditions() {
		return this.conditions;
	}

	public void setConditions(List<PredicateNode> conditions) {
		this.conditions = conditions;
	}

	public void setPredicates(List<PredicateNode> predicates) {
		this.predicates = predicates;
	}

	public void setElsePredicate(PredicateNode elsePredicate) {
		this.elsePredicate = elsePredicate;
	}

	String prepareToString(String selectIf, String whenElsif) {
		StringBuilder sb = new StringBuilder();
		sb.append(selectIf).append(" ").append(conditions.get(0)).append(" THEN ").append(predicates.get(0));
		for (int i = 1; i < conditions.size(); i++) {
			sb.append(" ").append(whenElsif).append(" ").append(conditions.get(i)).append(" THEN ")
					.append(predicates.get(i));
		}
		if (null != elsePredicate) {
			sb.append(" ELSE ").append(elsePredicate);
		}
		sb.append(" END");
		return sb.toString();
	}

}
