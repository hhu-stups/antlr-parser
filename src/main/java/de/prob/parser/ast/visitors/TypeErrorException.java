package de.prob.parser.ast.visitors;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.TypedNode;
import de.prob.parser.ast.types.BType;
import de.prob.parser.ast.types.UnificationException;

public class TypeErrorException extends Exception {
	private static final long serialVersionUID = -5344167922965323221L;

	public TypeErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public TypeErrorException(String message) {
		this(message, null);
	}

	public TypeErrorException(BType expected, BType found, TypedNode node, UnificationException e) {
		this(createErrorMessage(expected, found, node), e);
	}

	private static String createErrorMessage(BType expected, BType found, TypedNode node) {
		StringBuilder sb = new StringBuilder();
		sb.append("Expected ").append(expected).append(" but found ").append(found).append(" ");

		SourceCodePosition sourceCodePosition = node.getSourceCodePosition();
		int line = sourceCodePosition.getStartLine();
		int pos = sourceCodePosition.getStartColumn();
		String text = sourceCodePosition.getText();

		sb.append("at '").append(text).append("' starting ");
		sb.append("in line ").append(line);
		sb.append(" column ").append(pos).append(".");
		return sb.toString();
	}
}
