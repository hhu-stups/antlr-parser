package de.prob.parser.ast.types;

public final class StringType extends BasicType implements BType {

    private static final StringType instance = new StringType();

    public static StringType getInstance() {
        return instance;
    }

    private StringType() {
        super("STRING");
    }
}
