package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

public class IfExpressionNode extends ExprNode {

	protected PredicateNode condition;
	protected ExprNode thenExpr;
	protected ExprNode elseExpr;


	public IfExpressionNode(SourceCodePosition sourceCodePosition, PredicateNode condition,
							ExprNode thenExpr, ExprNode elseExpr) {
		super(sourceCodePosition);
		this.condition = condition;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;
		this.condition.setParent(this);
		this.thenExpr.setParent(this);
		this.elseExpr.setParent(this);
	}

	public ExprNode getThenExpression() {
		return this.thenExpr;
	}

	public ExprNode getElseExpression() {
		return this.elseExpr;
	}

	public PredicateNode getCondition() {
		return this.condition;
	}

	public void setCondition(PredicateNode condition) {
		this.condition = condition;
	}

	String prepareToString(String selectIf) {
		StringBuilder sb = new StringBuilder();
		sb.append(selectIf).append(" ").append(condition).append(" THEN ").append(thenExpr);
		sb.append(" ELSE ").append(elseExpr);
		sb.append(" END");
		return sb.toString();
	}

}
