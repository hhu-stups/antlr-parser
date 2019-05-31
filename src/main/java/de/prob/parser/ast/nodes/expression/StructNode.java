package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;

import java.util.List;

/**
 * Created by fabian on 25.05.19.
 */
public class StructNode extends ExprNode {

    private List<IdentifierExprNode> identifiers;

    private List<ExprNode> expressions;

    public StructNode(SourceCodePosition sourceCodePosition, List<IdentifierExprNode> identifiers, List<ExprNode> expressions) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("STRUCT(");
        for(int i = 0; i < identifiers.size(); i++) {
            sb.append(identifiers.get(i));
            sb.append(":");
            sb.append(expressions.get(i).toString());
            if(i < identifiers.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
