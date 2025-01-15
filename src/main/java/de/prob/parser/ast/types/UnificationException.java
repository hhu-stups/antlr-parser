package de.prob.parser.ast.types;

public class UnificationException extends Exception {
    private static final long serialVersionUID = 6100617838812319766L;

    public UnificationException(Throwable cause) {
        super(cause);
    }

    public UnificationException() {
        this(null);
    }
}
