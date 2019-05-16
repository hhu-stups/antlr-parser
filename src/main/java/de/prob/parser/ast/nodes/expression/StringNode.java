package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;

/**
 * Created by fabian on 16.05.19.
 */
public class StringNode extends ExprNode {

    private final String value;

    public StringNode(SourceCodePosition sourceCodePosition, String value) {
        super(sourceCodePosition);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
