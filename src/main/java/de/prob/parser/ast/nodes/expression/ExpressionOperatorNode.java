package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.OperatorNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExpressionOperatorNode extends ExprNode
		implements OperatorNode<ExpressionOperatorNode.ExpressionOperator> {

	public enum ExpressionOperator {

		// arithmetic
		NATURAL, NATURAL1, INTEGER, INT, NAT, NAT1, MININT, MAXINT//
		, TRUE, FALSE, POWER_OF //
		, PLUS, MINUS, MULT, DIVIDE, MOD, INTERVAL//
		, UNARY_MINUS//
		, MAX, MIN//
		, PRED, SUCC//
		//
		, BOOL
		// set operators
		, SET_ENUMERATION, EMPTY_SET, SET_SUBTRACTION, UNION, INTERSECTION//
		, GENERALIZED_UNION, GENERALIZED_INTER//
		// relations
		, DOMAIN, RANGE, ID, CLOSURE, CLOSURE1, ITERATE, CARTESIAN_PRODUCT//
		, CARD, DOMAIN_RESTRICTION, OVERWRITE_RELATION, DIRECT_PRODUCT, PARALLEL_PRODUCT, COMPOSITION//
		, DOMAIN_SUBTRACTION, RANGE_RESTRICTION, RANGE_SUBTRACTION//
		, INVERSE_RELATION, SET_RELATION, RELATIONAL_IMAGE, TOTAL_SURJECTION_RELATION, SURJECTION_RELATION
		// function
		, FUNCTION_CALL, PARTIAL_BIJECTION, PARTIAL_FUNCTION, PARTIAL_INJECTION
		, PARTIAL_SURJECTION, TOTAL_BIJECTION, TOTAL_FUNCTION
		, TOTAL_INJECTION, TOTAL_RELATION, TOTAL_SURJECTION
		// sequence operators
		, FIRST, LAST, FRONT, TAIL, CONC, SEQ_ENUMERATION, EMPTY_SEQUENCE//
		, CONCAT, INSERT_FRONT, INSERT_TAIL, RESTRICT_FRONT, RESTRICT_TAIL//
		, SEQ, SEQ1, ISEQ, ISEQ1, SIZE, PERM, REV
		// special
		, COUPLE
		// prefix operators
		, FIN, POW
	}

	private List<ExprNode> expressionNodes;

	private ExpressionOperator operator;

	public ExpressionOperatorNode(SourceCodePosition sourceCodePosition, List<ExprNode> expressionNodes,
			ExpressionOperator operator) {
		// used for set enumeration, e.g. {1,2,3}
		super(sourceCodePosition);
		setExpressionNodes(expressionNodes);
		this.expressionNodes = expressionNodes;
		this.operator = operator;
		this.expressionNodes.forEach(expr -> expr.setParent(this));
	}

	public ExpressionOperatorNode(SourceCodePosition sourceCodePosition, ExpressionOperator operator) {
		super(sourceCodePosition);
		this.expressionNodes = new ArrayList<>();
		this.operator = operator;
	}

	@Override
	public ExpressionOperator getOperator() {
		return operator;
	}

	@Override
	public void setOperator(ExpressionOperator operator) {
		this.operator = operator;
	}

	public void setExpressionNodes(List<ExprNode> expressionNodes) {
		this.expressionNodes = expressionNodes;
		for (ExprNode exprNode : expressionNodes) {
			exprNode.setParent(this);
		}
	}

	public List<ExprNode> getExpressionNodes() {
		return expressionNodes;
	}

	public int getArity() {
		return expressionNodes.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.operator.name());
		Iterator<ExprNode> iter = expressionNodes.iterator();
		if (iter.hasNext()) {
			sb.append("(");
			while (iter.hasNext()) {
				sb.append(iter.next().toString());
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}
		return sb.toString();
	}

	public void setExpressionList(List<ExprNode> list) {
		this.expressionNodes = list;
	}

}
