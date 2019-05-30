package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;

/**
 * Created by fabian on 30.05.19.
 */
public class RecordFieldAccessNode extends ExprNode {

    private ExprNode record;

    private IdentifierExprNode identifier;

    public RecordFieldAccessNode(SourceCodePosition sourceCodePosition, ExprNode record, IdentifierExprNode identifier) {
        super(sourceCodePosition);
        this.record = record;
        this.identifier = identifier;
    }

    public ExprNode getRecord() {
        return record;
    }

    public IdentifierExprNode getIdentifier() {
        return identifier;
    }
}
