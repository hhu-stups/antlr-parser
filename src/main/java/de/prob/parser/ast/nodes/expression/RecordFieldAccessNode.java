package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;

/**
 * Created by fabian on 30.05.19.
 */
public class RecordFieldAccessNode extends ExprNode {

    private ExprNode record;

    private DeclarationNode identifier;

    public RecordFieldAccessNode(SourceCodePosition sourceCodePosition, ExprNode record, DeclarationNode identifier) {
        super(sourceCodePosition);
        this.record = record;
        this.identifier = identifier;
    }

    public ExprNode getRecord() {
        return record;
    }

    public DeclarationNode getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RECORD_FIELD_ACCESS(");
        sb.append(record.toString());
        sb.append(", ");
        sb.append(identifier.toString());
        sb.append(")");
        return sb.toString();
    }
}
