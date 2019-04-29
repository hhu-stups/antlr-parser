package de.prob.parser.ast.nodes.predicate;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;

import java.util.List;

public class LetPredicateNode extends PredicateNode {

	private List<DeclarationNode> localVariables;
	private PredicateNode wherePredicate;
	private PredicateNode predicate;

	public LetPredicateNode(SourceCodePosition sourceCodePosition, List<DeclarationNode> localIdentifiers,
                            PredicateNode wherePredicate, PredicateNode predicate) {
		super(sourceCodePosition);
		this.localVariables = localIdentifiers;
		this.wherePredicate = wherePredicate;
		this.predicate = predicate;
		this.localVariables.forEach(node -> node.setParent(this));
		this.wherePredicate.setParent(this);
		this.predicate.setParent(this);
	}

	public List<DeclarationNode> getLocalIdentifiers() {
		return localVariables;
	}

	public PredicateNode getWherePredicate() {
		return wherePredicate;
	}

	public PredicateNode getPredicate() {
		return predicate;
	}

	public void setPredicate(PredicateNode predicate) {
		this.predicate = predicate;
	}

	@Override
	public String toString() {
		return "LET " + localVariables + " BE " + wherePredicate + " IN " + predicate + " END";
	}

}
