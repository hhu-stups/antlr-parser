package de.prob.parser.ast.types;

import java.util.Objects;

public abstract class SetElementType implements BType {

    private final String setName;

    SetElementType(String name) {
        this.setName = Objects.requireNonNull(name, "name");
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (!this.unifiable(otherType)) {
            throw new UnificationException();
        }
        if (otherType instanceof UntypedType) {
            ((UntypedType) otherType).replaceBy(this);
        }
        return this;
    }

    public String getSetName() {
        return this.setName;
    }

    @Override
    public String toString() {
        return this.setName;
    }

    @Override
    public boolean unifiable(BType otherType) {
        return otherType instanceof UntypedType || this.equals(otherType);
    }

    @Override
    public final boolean contains(BType other) {
        return false;
    }

    @Override
    public final boolean isUntyped() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SetElementType) {
            return this.setName.equals(((SetElementType) obj).getSetName());
        }
        return false;
    }
}
