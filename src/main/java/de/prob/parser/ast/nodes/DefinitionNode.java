package de.prob.parser.ast.nodes;

import java.util.List;

import de.prob.parser.ast.SourceCodePosition;

public class DefinitionNode extends TypedNode {

	private final String name;
	private final List<DeclarationNode> params;
	private final Node body;

	public DefinitionNode(SourceCodePosition sourceCodePosition, String name, List<DeclarationNode> paramNodes,
			Node body) {
		super(sourceCodePosition);
		this.name = name;
		this.params = paramNodes;
		this.params.forEach(param -> param.setParent(this));
		this.body = body;
	}

	public String getName() {
		return this.name;
	}

	public List<DeclarationNode> getParams() {
		return params;
	}

	public Node getBody() {
		return this.body;
	}

}
