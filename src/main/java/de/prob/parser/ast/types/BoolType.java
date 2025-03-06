package de.prob.parser.ast.types;

public final class BoolType extends BasicType implements BType {

    private static final BoolType instance = new BoolType();

    public static BoolType getInstance() {
        return instance;
    }

    private BoolType() {
        super("BOOL");
    }
}
