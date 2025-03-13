package de.prob.parser.ast.nodes.substitution;

import de.prob.parser.ast.SourceCodePosition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fabian on 13.10.18.
 */
public class ChoiceSubstitutionNode extends SubstitutionNode {

    private List<SubstitutionNode> substitutions;

    public ChoiceSubstitutionNode(SourceCodePosition sourceCodePosition, List<SubstitutionNode> substitutions) {
        super(sourceCodePosition);
        this.substitutions = substitutions;
        this.substitutions.forEach(sub -> sub.setParent(this));
    }

    public List<SubstitutionNode> getSubstitutions() {
        return substitutions;
    }

    @Override
    public String toString() {
        return "CHOICE" + substitutions.stream().map(Object::toString).collect(Collectors.joining(",", "(", ")"));
    }
}
