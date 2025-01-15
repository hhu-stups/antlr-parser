package de.prob.parser.antlr;

public class ScopeException extends Exception {
	private static final long serialVersionUID = 6584928829237049955L;

	public ScopeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScopeException(String message) {
		this(message, null);
	}
}
