package de.prob.parser.ast.types;

public final class IntegerType extends BasicType implements BType {

    private static final IntegerType instance = new IntegerType();

    public static IntegerType getInstance() {
        return instance;
    }

    private IntegerType() {
        super("INTEGER");
    }

    @Override
    public boolean unifiable(BType otherType) {
        return super.unifiable(otherType) || otherType instanceof SetOrIntegerType || otherType instanceof IntegerOrSetOfPairs;
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (otherType instanceof SetOrIntegerType || otherType instanceof IntegerOrSetOfPairs) {
            return otherType.unify(this);
        } else {
            return super.unify(otherType);
        }
    }
}
