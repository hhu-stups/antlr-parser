package de.prob.parser.ast.types;

import java.util.Observable;

public final class UntypedType extends Observable implements BType {

    @Override
    public boolean unifiable(BType otherType) {
        if (otherType instanceof UntypedType) {
            return true;
        } else {
            return otherType.unifiable(this);
        }
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (this.unifiable(otherType)) {
            if (otherType instanceof UntypedType) {
                ((UntypedType) otherType).replaceBy(this);
                return this;
            } else {
                return otherType.unify(this);
            }
        } else {
            throw new UnificationException();
        }
    }

    public void replaceBy(BType otherType) {
        this.setChanged();
        this.notifyObservers(otherType);
    }

    @Override
    public boolean isUntyped() {
        return true;
    }

    @Override
    public boolean contains(BType other) {
        return false;
    }

    @Override
    public String toString() {
        int h = this.hashCode();
        return "_Type" + ((h ^ (h >>> 16)) & 0xFFFF) + "_";
    }
}
