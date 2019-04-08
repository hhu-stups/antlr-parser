package de.prob.parser.ast.nodes.substitution;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

import java.util.List;

public class LetSubstitutionNode extends SubstitutionNode {

	private List<DeclarationNode> localVariables;
	private PredicateNode predicate;
	private SubstitutionNode body;

	public LetSubstitutionNode(SourceCodePosition sourceCodePosition, List<DeclarationNode> localIdentifiers,
                               PredicateNode predicate, SubstitutionNode body) {
		super(sourceCodePosition);
		this.localVariables = localIdentifiers;
		this.predicate = predicate;
		this.body = body;
		this.localVariables.forEach(node -> node.setParent(this));
		this.predicate.setParent(this);
		this.body.setParent(this);
	}

	public List<DeclarationNode> getLocalIdentifiers() {
		return localVariables;
	}

	public SubstitutionNode getBody() {
		return body;
	}

	public PredicateNode getPredicate() {
		return predicate;
	}

	public void setSubstitution(SubstitutionNode substitutionNode) {
		this.body = substitutionNode;
	}

	@Override
	public String toString() {
		return "LET " + localVariables + " BE " + predicate + " IN " + body + " END";
	}

}
