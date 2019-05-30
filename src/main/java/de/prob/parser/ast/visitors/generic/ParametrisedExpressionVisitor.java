package de.prob.parser.ast.visitors.generic;

import de.prob.parser.ast.nodes.expression.RecordNode;
import de.prob.parser.ast.nodes.expression.StructNode;
import de.prob.parser.ast.nodes.expression.ExprNode;
import de.prob.parser.ast.nodes.expression.ExpressionOperatorNode;
import de.prob.parser.ast.nodes.expression.IdentifierExprNode;
import de.prob.parser.ast.nodes.expression.IfExpressionNode;
import de.prob.parser.ast.nodes.expression.LambdaNode;
import de.prob.parser.ast.nodes.expression.LetExpressionNode;
import de.prob.parser.ast.nodes.expression.NumberNode;
import de.prob.parser.ast.nodes.expression.QuantifiedExpressionNode;
import de.prob.parser.ast.nodes.expression.SetComprehensionNode;
import de.prob.parser.ast.nodes.expression.StringNode;
import de.prob.parser.ast.nodes.predicate.CastPredicateExpressionNode;

public interface ParametrisedExpressionVisitor<R, P> {

	default R visitExprNode(ExprNode node, P expected) {
		if (node instanceof ExpressionOperatorNode) {
			return visitExprOperatorNode((ExpressionOperatorNode) node, expected);
		} else if (node instanceof IdentifierExprNode) {
			return visitIdentifierExprNode((IdentifierExprNode) node, expected);
		} else if (node instanceof NumberNode) {
			return visitNumberNode((NumberNode) node, expected);
		} else if (node instanceof QuantifiedExpressionNode) {
			return visitQuantifiedExpressionNode((QuantifiedExpressionNode) node, expected);
		} else if (node instanceof SetComprehensionNode) {
			return visitSetComprehensionNode((SetComprehensionNode) node, expected);
		} else if (node instanceof CastPredicateExpressionNode) {
			return visitCastPredicateExpressionNode((CastPredicateExpressionNode) node, expected);
		} else if (node instanceof LambdaNode) {
			return visitLambdaNode((LambdaNode) node, expected);
		} else if(node instanceof LetExpressionNode) {
			return visitLetExpressionNode((LetExpressionNode) node, expected);
		} else if(node instanceof IfExpressionNode) {
			return visitIfExpressionNode((IfExpressionNode) node, expected);
		} else if(node instanceof StringNode) {
			return visitStringNode((StringNode) node, expected);
		} else if(node instanceof RecordNode) {
			return visitRecordNode((RecordNode) node, expected);
		} else if(node instanceof StructNode) {
			return visitStructNode((StructNode) node, expected);
		}
		throw new AssertionError(node.getClass());
	}

	R visitExprOperatorNode(ExpressionOperatorNode node, P expected);

	R visitIdentifierExprNode(IdentifierExprNode node, P expected);

	R visitCastPredicateExpressionNode(CastPredicateExpressionNode node, P expected);

	R visitNumberNode(NumberNode node, P expected);

	R visitQuantifiedExpressionNode(QuantifiedExpressionNode node, P expected);

	R visitSetComprehensionNode(SetComprehensionNode node, P expected);

	R visitLambdaNode(LambdaNode node, P expected);

	R visitLetExpressionNode(LetExpressionNode node, P expected);

	R visitIfExpressionNode(IfExpressionNode node, P expected);

	R visitStringNode(StringNode node, P expected);

	R visitRecordNode(RecordNode node, P expected);

	R visitStructNode(StructNode node, P expected);
}
