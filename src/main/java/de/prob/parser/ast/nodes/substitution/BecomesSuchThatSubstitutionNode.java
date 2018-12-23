package de.prob.parser.ast.nodes.substitution;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.expression.IdentifierExprNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

import java.util.List;
import java.util.stream.Collectors;

public class BecomesSuchThatSubstitutionNode extends SubstitutionNode {
	private List<IdentifierExprNode> identifiers;
	private PredicateNode predicate;

	public BecomesSuchThatSubstitutionNode(SourceCodePosition sourceCodePosition, List<IdentifierExprNode> identifiers,
			PredicateNode predicate) {
		super(sourceCodePosition);
		this.identifiers = identifiers;
		this.predicate = predicate;
		identifiers.forEach(id -> id.setParent(this));
		predicate.setParent(this);
	}

	public List<IdentifierExprNode> getIdentifiers() {
		return identifiers;
	}

	public PredicateNode getPredicate() {
		return predicate;
	}

	public void setPredicate(PredicateNode predicate) {
		this.predicate = predicate;
	}

	@Override
	public String toString() {
		return identifiers.stream().map(Object::toString).collect(Collectors.joining(",")) + " :( " + predicate + ")";
	}

}
