package de.prob.parser.ast.types;

public class StringType extends BasicType implements BType {

    private static StringType instance = new StringType();

    public static StringType getInstance() {
        return instance;
    }

    private StringType() {
        super("STRING");
    }

    @Override
    public boolean unifiable(BType otherType) {
        return otherType == this || otherType instanceof UntypedType;
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (unifiable(otherType)) {
            if (otherType == instance) {
                return instance;
            } else {
                ((UntypedType) otherType).replaceBy(this);
                return instance;
            }
        } else {
            throw new UnificationException();
        }
    }
}
