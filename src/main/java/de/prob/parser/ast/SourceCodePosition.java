package de.prob.parser.ast;

public class SourceCodePosition {
	private final int startLine;
	private final int startColumn;
	private final String text;

	public SourceCodePosition(int startLine, int startColumn, String text) {
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.text = text;
	}

	public int getStartLine() {
		return this.startLine;
	}

	public int getStartColumn() {
		return this.startColumn;
	}

	public String getText() {
		return this.text;
	}
}
