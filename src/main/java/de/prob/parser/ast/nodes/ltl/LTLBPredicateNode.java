package de.prob.parser.ast.nodes.ltl;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

public class LTLBPredicateNode extends LTLNode {

	private PredicateNode predicate;

	public LTLBPredicateNode(SourceCodePosition sourceCodePosition, PredicateNode predicate) {
		super(sourceCodePosition);
		this.predicate = predicate;
		this.predicate.setParent(this);
	}

	public PredicateNode getPredicate() {
		return this.predicate;
	}

	public void setPredicateNode(PredicateNode predicate) {
		this.predicate = predicate;
	}

	@Override
	public String toString() {
		return this.predicate.toString();
	}

}
