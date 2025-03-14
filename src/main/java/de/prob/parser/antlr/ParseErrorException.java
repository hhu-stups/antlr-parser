package de.prob.parser.antlr;

import org.antlr.v4.runtime.CommonToken;

public class ParseErrorException extends Exception {
    private static final long serialVersionUID = 2305560853973886094L;
    private final CommonToken token;

    public ParseErrorException(CommonToken token, String message, Throwable cause) {
        super(message, cause);
        this.token = token;
    }

    public ParseErrorException(CommonToken token, String message) {
        this(token, message, null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parse error: Unexpected input '").append(token.getText()).append("' ");
        sb.append("in line ").append(token.getLine());
        sb.append(" column " + token.getCharPositionInLine()).append(".\n");
        sb.append("Additional information: ").append(super.getMessage());
        return sb.toString();
    }
}
