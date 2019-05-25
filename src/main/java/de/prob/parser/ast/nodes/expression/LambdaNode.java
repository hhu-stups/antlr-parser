package de.prob.parser.ast.nodes.expression;


import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fabian on 05.02.19.
 */
public class LambdaNode extends ExprNode {

    private List<DeclarationNode> declarationList;

    private PredicateNode predicateNode;

    private ExprNode expressionNode;

    public LambdaNode(SourceCodePosition sourceCodePosition, List<DeclarationNode> declarationList,
                      PredicateNode predicateNode, ExprNode expressionNode) {
        super(sourceCodePosition);
        this.declarationList = declarationList;
        this.predicateNode = predicateNode;
        this.expressionNode = expressionNode;
        this.declarationList.forEach(decl -> decl.setParent(this));
        this.predicateNode.setParent(this);
        this.expressionNode.setParent(this);
    }

    public List<DeclarationNode> getDeclarations() {
        return declarationList;
    }

    public PredicateNode getPredicate() {
        return predicateNode;
    }

    public ExprNode getExpression() {
        return expressionNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LAMBDA(");

        sb.append(declarationList.stream().map(Object::toString).collect(Collectors.joining(",")));
        sb.append(".");
        sb.append(predicateNode);
        sb.append("|");
        sb.append(expressionNode);
        sb.append(")");
        return sb.toString();
    }
}
