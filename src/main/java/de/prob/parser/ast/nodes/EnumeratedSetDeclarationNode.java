package de.prob.parser.ast.nodes;

import java.util.List;
import java.util.stream.Collectors;

import de.prob.parser.ast.SourceCodePosition;

public class EnumeratedSetDeclarationNode extends Node {
	final DeclarationNode setDeclaration;
	final List<DeclarationNode> elements;

	public EnumeratedSetDeclarationNode(SourceCodePosition sourceCodePosition, DeclarationNode setDeclaration,
			List<DeclarationNode> elements) {
		super(sourceCodePosition);
		this.setDeclaration = setDeclaration;
		this.elements = elements;
	}

	public DeclarationNode getSetDeclaration() {
		return this.setDeclaration;
	}

	public List<DeclarationNode> getElements() {
		return this.elements;
	}

	public List<String> getElementsAsStrings() {
		return elements.stream().map(DeclarationNode::getName).collect(Collectors.toList());
	}

	@Override
	public void removeChild(Node child) {
		elements.remove(child);
	}

}