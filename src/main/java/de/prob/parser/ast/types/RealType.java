package de.prob.parser.ast.types;

public class RealType extends BasicType implements BType {

    private static RealType instance = new RealType();

    public static RealType getInstance() {
        return instance;
    }

    private RealType() {
        super("REAL");
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
