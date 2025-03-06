package de.prob.parser.ast.types;

import java.util.Objects;

public abstract class BasicType implements BType {

    private final String stringRepresentation;

    BasicType(String stringRepresentation) {
        this.stringRepresentation = Objects.requireNonNull(stringRepresentation, "stringRepresentation");
    }

    @Override
    public final boolean isUntyped() {
        return false;
    }

    @Override
    public final boolean contains(BType other) {
        return false;
    }

    @Override
    public boolean unifiable(BType otherType) {
        return otherType instanceof UntypedType || this.equals(otherType);
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (this.unifiable(otherType)) {
            if (otherType instanceof UntypedType) {
                ((UntypedType) otherType).replaceBy(this);
            }
            return this;
        } else {
            throw new UnificationException();
        }
    }

    @Override
    public final boolean equals(Object obj) {
        return this.getClass().isInstance(obj);
    }

    @Override
    public final String toString() {
        return this.stringRepresentation;
    }
}
