package de.prob.parser.ast.types;

public final class RealType extends BasicType implements BType {

    private static final RealType instance = new RealType();

    public static RealType getInstance() {
        return instance;
    }

    private RealType() {
        super("REAL");
    }
}
