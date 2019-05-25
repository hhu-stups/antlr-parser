package de.prob.parser.ast.nodes;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.expression.ExprNode;
import de.prob.parser.ast.nodes.expression.IdentifierExprNode;

import java.util.List;

/**
 * Created by fabian on 25.05.19.
 */
public class RecordNode extends ExprNode {

    private List<IdentifierExprNode> identifiers;

    private List<ExprNode> expressions;

    public RecordNode(SourceCodePosition sourceCodePosition, List<IdentifierExprNode> identifiers, List<ExprNode> expressions) {
        super(sourceCodePosition);
        this.identifiers = identifiers;
        this.expressions = expressions;
    }

    public List<IdentifierExprNode> getIdentifiers() {
        return identifiers;
    }

    public List<ExprNode> getExpressions() {
        return expressions;
    }
}
