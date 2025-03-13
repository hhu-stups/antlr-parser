package de.prob.parser.ast.nodes;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.expression.ExprNode;

public class EnumeratedSetElementNode extends ExprNode {

    final EnumeratedSetDeclarationNode enumeratedSetDeclarationNode;
    final DeclarationNode declarationNode;
    final String elementName;

    public EnumeratedSetElementNode(SourceCodePosition sourceCodePosition,
                                    EnumeratedSetDeclarationNode enumeratedSetDeclarationNode, String elementName,
                                    DeclarationNode declarationNode) {
        super(sourceCodePosition);
        this.enumeratedSetDeclarationNode = enumeratedSetDeclarationNode;
        this.elementName = elementName;
        this.declarationNode = declarationNode;
        this.enumeratedSetDeclarationNode.setParent(this);
        this.declarationNode.setParent(this);
    }

    public String getName() {
        return this.elementName;
    }

    public EnumeratedSetDeclarationNode getEnumeratedSetDeclarationNode() {
        return this.enumeratedSetDeclarationNode;
    }

    public DeclarationNode getDeclarationNode() {
        return declarationNode;
    }

    @Override
    public String toString() {
        return elementName;
    }
}
