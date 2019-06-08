package de.prob.parser.ast.nodes.substitution;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.expression.ExprNode;

import java.util.List;

public class AssignSubstitutionNode extends SubstitutionNode {

	private List<ExprNode> leftSide;
	private List<ExprNode> rightSide;

	public AssignSubstitutionNode(SourceCodePosition sourceCodePosition, List<ExprNode> leftSide,
			List<ExprNode> rightSide) {
		super(sourceCodePosition);
		this.leftSide = leftSide;
		this.rightSide = rightSide;
		leftSide.forEach(node -> node.setParent(this));
		rightSide.forEach(node -> node.setParent(this));
	}

	public List<ExprNode> getLeftSide() {
		return this.leftSide;
	}

	public List<ExprNode> getRightSide() {
		return this.rightSide;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (ExprNode node : leftSide) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append(node);
		}
		sb.append(":=");

		first = true;
		for (ExprNode node : rightSide) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append(node);
		}
		return sb.toString();
	}

}
