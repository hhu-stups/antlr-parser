package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.predicate.PredicateNode;

import java.util.List;

public class IfExpressionNode extends ExprNode {

	protected List<PredicateNode> conditions;
	protected List<ExprNode> expressions;
	protected ExprNode elseExpr;


	public IfExpressionNode(SourceCodePosition sourceCodePosition, List<PredicateNode> conditions,
							List<ExprNode> expressions, ExprNode elseExpr) {
		super(sourceCodePosition);
		this.conditions = conditions;
		this.expressions = expressions;
		this.elseExpr = elseExpr;
		this.conditions.forEach(cond -> cond.setParent(this));
		this.expressions.forEach(sub -> sub.setParent(this));
		if(elseExpr != null) {
			this.elseExpr.setParent(this);
		}
	}

	public List<ExprNode> getExpressions() {
		return this.expressions;
	}

	public ExprNode getElseExpression() {
		return this.elseExpr;
	}

	public List<PredicateNode> getConditions() {
		return this.conditions;
	}

	public void setConditions(List<PredicateNode> conditions) {
		this.conditions = conditions;
	}

	public void setExpressions(List<ExprNode> expressions) {
		this.expressions = expressions;
	}

	public void setElseExpression(ExprNode elseExpr) {
		this.elseExpr = elseExpr;
	}

	String prepareToString(String selectIf, String whenElsif) {
		StringBuilder sb = new StringBuilder();
		sb.append(selectIf).append(" ").append(conditions.get(0)).append(" THEN ").append(expressions.get(0));
		for (int i = 1; i < conditions.size(); i++) {
			sb.append(" ").append(whenElsif).append(" ").append(conditions.get(i)).append(" THEN ")
					.append(expressions.get(i));
		}
		if (null != elseExpr) {
			sb.append(" ELSE ").append(elseExpr);
		}
		sb.append(" END");
		return sb.toString();
	}

}
