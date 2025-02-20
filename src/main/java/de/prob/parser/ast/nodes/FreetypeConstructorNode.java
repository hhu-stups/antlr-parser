package de.prob.parser.ast.nodes;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.expression.ExprNode;

public class FreetypeConstructorNode extends FreetypeBaseElementNode {

    private final ExprNode expr;

    public FreetypeConstructorNode(SourceCodePosition sourceCodePosition, String name, MachineNode machineNode, ExprNode expr) {
        super(sourceCodePosition, name, machineNode);
        this.expr = expr;
        this.expr.setParent(this);
    }

    public ExprNode getExpr() {
        return this.expr;
    }
}
