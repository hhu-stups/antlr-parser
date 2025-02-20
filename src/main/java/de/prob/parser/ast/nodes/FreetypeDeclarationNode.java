package de.prob.parser.ast.nodes;

import de.prob.parser.ast.SourceCodePosition;

import java.util.List;

public class FreetypeDeclarationNode extends Node {
	final DeclarationNode freetypeDeclaration;
	final List<FreetypeBaseElementNode> elements;

	public FreetypeDeclarationNode(SourceCodePosition sourceCodePosition, DeclarationNode freetypeDeclaration, List<FreetypeBaseElementNode> elements) {
		super(sourceCodePosition);
		this.freetypeDeclaration = freetypeDeclaration;
		this.elements = elements;
		this.freetypeDeclaration.setParent(this);
		this.elements.forEach(element -> element.setParent(this));
	}

	public DeclarationNode getFreetypeDeclarationNode() {
		return this.freetypeDeclaration;
	}

	public List<FreetypeBaseElementNode> getElements() {
		return this.elements;
	}

	@Override
	public void removeChild(Node child) {
		elements.remove(child);
	}
}
