package de.prob.parser.ast.nodes;

import de.prob.parser.ast.SourceCodePosition;

public class FreetypeElementNode extends FreetypeBaseElementNode {

    public FreetypeElementNode(SourceCodePosition sourceCodePosition, String name, MachineNode machineNode) {
        super(sourceCodePosition, name, machineNode);
    }
}
