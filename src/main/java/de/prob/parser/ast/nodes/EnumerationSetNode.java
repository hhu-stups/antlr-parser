package de.prob.parser.ast.nodes;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.expression.ExprNode;

public class EnumerationSetNode extends ExprNode {

	final EnumeratedSetDeclarationNode enumeratedSetDeclarationNode;
	final String name;

	public EnumerationSetNode(SourceCodePosition sourceCodePosition, EnumeratedSetDeclarationNode enumeratedSetNode,
			String name) {
		super(sourceCodePosition);
		this.enumeratedSetDeclarationNode = enumeratedSetNode;
		this.name = name;
		this.enumeratedSetDeclarationNode.setParent(this);
	}

	public String getName() {
		return this.name;
	}

	public EnumeratedSetDeclarationNode getEnumeratedSetDeclarationNode() {
		return this.enumeratedSetDeclarationNode;
	}

}
