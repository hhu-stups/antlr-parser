package de.prob.parser.ast.visitors.generic;

import de.prob.parser.ast.nodes.expression.RecordFieldAccessNode;
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

public interface ExpressionVisitor {

	default void visitExprNode(ExprNode node) {
		if (node instanceof ExpressionOperatorNode) {
			visitExprOperatorNode((ExpressionOperatorNode) node);
		} else if (node instanceof IdentifierExprNode) {
			visitIdentifierExprNode((IdentifierExprNode) node);
		} else if (node instanceof NumberNode) {
			visitNumberNode((NumberNode) node);
		} else if (node instanceof QuantifiedExpressionNode) {
			visitQuantifiedExpressionNode((QuantifiedExpressionNode) node);
		} else if (node instanceof SetComprehensionNode) {
			visitSetComprehensionNode((SetComprehensionNode) node);
		} else if (node instanceof CastPredicateExpressionNode) {
			visitCastPredicateExpressionNode((CastPredicateExpressionNode) node);
		} else if(node instanceof LambdaNode) {
			visitLambdaNode((LambdaNode) node);
		} else if(node instanceof LetExpressionNode) {
			visitLetExpressionNode((LetExpressionNode) node);
		} else if(node instanceof IfExpressionNode) {
			visitIfExpressionNode((IfExpressionNode) node);
		} else if(node instanceof StringNode) {
			visitStringNode((StringNode) node);
		} else if(node instanceof RecordNode) {
			visitRecordNode((RecordNode) node);
		} else if(node instanceof StructNode) {
			visitStructNode((StructNode) node);
		} else if(node instanceof RecordFieldAccessNode) {
			visitRecordFieldAccessNode((RecordFieldAccessNode) node);
		} else {
			throw new AssertionError(node.getClass());
		}
		
	}

	void visitExprOperatorNode(ExpressionOperatorNode node);

	void visitIdentifierExprNode(IdentifierExprNode node);

	void visitCastPredicateExpressionNode(CastPredicateExpressionNode node);

	void visitNumberNode(NumberNode node);

	void visitQuantifiedExpressionNode(QuantifiedExpressionNode node);

	void visitSetComprehensionNode(SetComprehensionNode node);

	void visitLambdaNode(LambdaNode node);

	void visitLetExpressionNode(LetExpressionNode node);

	void visitIfExpressionNode(IfExpressionNode node);

	void visitStringNode(StringNode node);

	void visitRecordNode(RecordNode node);

	void visitStructNode(StructNode node);

	void visitRecordFieldAccessNode(RecordFieldAccessNode node);

}
