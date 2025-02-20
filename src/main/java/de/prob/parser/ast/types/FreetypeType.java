package de.prob.parser.ast.types;

public final class FreetypeType implements BType {

	private final String name;

	public FreetypeType(String name) {
		this.name = name;
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

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean unifiable(BType otherType) {
		if (otherType instanceof UntypedType) {
			return true;
		}
		return this.equals(otherType);
	}

	@Override
	public boolean contains(BType other) {
		return false;
	}

	@Override
	public boolean isUntyped() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FreetypeType) {
			return this.name.equals(((FreetypeType) obj).name);
		}
		return false;
	}
}
