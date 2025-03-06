package de.prob.parser.ast.types;

public interface BType {

    BType unify(BType otherType) throws UnificationException;

    boolean unifiable(BType otherType);

    boolean contains(BType other);

    boolean isUntyped();

}
