package de.prob.parser.ast.types;

public class UnificationException extends Exception {

    public UnificationException() {
        super();
    }

    public UnificationException(String message) {
        super(message);
    }

    public UnificationException(Throwable cause) {
        super(cause);
    }
}
