package de.prob.parser.ast.nodes.substitution;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

public class ConditionSubstitutionNode extends SubstitutionNode {
	private Kind kind;
	private PredicateNode condition;
	private SubstitutionNode substitution;

	public enum Kind {
		PRECONDITION, ASSERT
	}

	public ConditionSubstitutionNode(SourceCodePosition sourceCodePosition, Kind kind, PredicateNode condition,
			SubstitutionNode substitution) {
		super(sourceCodePosition);
		this.condition = condition;
		this.substitution = substitution;
		this.kind = kind;
		this.condition.setParent(this);
		this.setParent(this);
	}

	public SubstitutionNode getSubstitution() {
		return substitution;
	}

	public PredicateNode getCondition() {
		return condition;
	}

	public Kind getKind() {
		return this.kind;
	}

	@Override
	public String toString() {
		if(this.kind == Kind.PRECONDITION) {
			return "PRE " + condition + " THEN " + substitution + " END";
		} else {
			return "ASSERT " + condition + " THEN " + substitution + " END";
		}
	}

	public void setSubstitution(SubstitutionNode substitution) {
		this.substitution = substitution;
	}

	public void setCondition(PredicateNode predicate) {
		this.condition = predicate;
	}

}
