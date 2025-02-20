package de.prob.parser.ast.nodes;

import de.prob.parser.ast.SourceCodePosition;

public abstract class FreetypeBaseElementNode extends DeclarationNode {

    protected FreetypeBaseElementNode(SourceCodePosition sourceCodePosition, String name, MachineNode machineNode) {
        super(sourceCodePosition, name, Kind.FREETYPE_ELEMENT, machineNode);
    }
}
