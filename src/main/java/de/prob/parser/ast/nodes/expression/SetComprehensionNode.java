package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

import java.util.List;
import java.util.stream.Collectors;

public class SetComprehensionNode extends ExprNode {
	protected List<DeclarationNode> declarationList;
	protected PredicateNode predicateNode;

	public SetComprehensionNode(SourceCodePosition sourceCodePosition, List<DeclarationNode> declarationList,
			PredicateNode predicateNode) {
		super(sourceCodePosition);
		this.declarationList = declarationList;
		this.predicateNode = predicateNode;
		this.declarationList.forEach(decl -> decl.setParent(this));
		this.predicateNode.setParent(this);
	}

	public List<DeclarationNode> getDeclarationList() {
		return declarationList;
	}

	public PredicateNode getPredicateNode() {
		return predicateNode;
	}

	public void setPredicate(PredicateNode newPredicate) {
		predicateNode = newPredicate;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SET_COMPREHENSION(");
		sb.append(declarationList.stream().map(Object::toString).collect(Collectors.joining(",")));
		sb.append(",");
		sb.append(predicateNode);
		sb.append(")");
		return sb.toString();
	}
}
