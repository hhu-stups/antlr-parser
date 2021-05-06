package de.prob.parser.ast.nodes;

import de.prob.parser.ast.SourceCodePosition;

public class OperationReferenceNode extends Node {

	private final String prefix;
	private final String operationName;
	private MachineNode machineNode;

	public OperationReferenceNode(SourceCodePosition sourceCodePosition, String operationName, String prefix,
                                  boolean explicitly) {
		super(sourceCodePosition);
		this.operationName = operationName;
		this.prefix = prefix;
	}

	public String getOperationName() {
		return operationName;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public String toString() {
		if(prefix != null) {
			return prefix + "." + operationName;
		}
		return operationName;
	}

	public void setMachineNode(MachineNode machineNode) {
		this.machineNode = machineNode;
	}

	public MachineNode getMachineNode() {
		return this.machineNode;
	}

}
