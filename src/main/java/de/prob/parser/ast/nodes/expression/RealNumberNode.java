package de.prob.parser.ast.nodes.expression;

import de.prob.parser.ast.SourceCodePosition;

import java.math.BigDecimal;

public class RealNumberNode extends ExprNode {

	private final BigDecimal value;

	public RealNumberNode(SourceCodePosition sourceCodePosition, BigDecimal value) {
		super(sourceCodePosition);
		this.value = value;
	}

	public BigDecimal getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
