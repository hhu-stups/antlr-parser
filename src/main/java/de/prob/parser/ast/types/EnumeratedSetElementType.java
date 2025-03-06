package de.prob.parser.ast.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EnumeratedSetElementType extends SetElementType implements BType {

    private final List<String> elements;

    public EnumeratedSetElementType(String name, List<String> list) {
        super(name);
        this.elements = Collections.unmodifiableList(new ArrayList<>(list));
    }

    public List<String> getElements() {
        return this.elements;
    }
}
