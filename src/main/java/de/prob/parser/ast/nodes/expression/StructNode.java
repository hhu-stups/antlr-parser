package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;

import java.util.List;

/**
 * Created by fabian on 25.05.19.
 */
public class StructNode extends ExprNode {

    private List<DeclarationNode> declarations;

    private List<ExprNode> expressions;

    public StructNode(SourceCodePosition sourceCodePosition, List<DeclarationNode> declarations, List<ExprNode> expressions) {
        super(sourceCodePosition);
        this.declarations = declarations;
        this.expressions = expressions;
    }

    public List<DeclarationNode> getDeclarations() {
        return declarations;
    }

    public List<ExprNode> getExpressions() {
        return expressions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("STRUCT(");
        for(int i = 0; i < declarations.size(); i++) {
            sb.append(declarations.get(i));
            sb.append(":");
            sb.append(expressions.get(i).toString());
            if(i < declarations.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
