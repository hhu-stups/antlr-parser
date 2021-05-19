package de.prob.parser.antlr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import de.prob.parser.ast.nodes.MachineNode;
import de.prob.parser.ast.nodes.expression.ExprNode;

public class BProject {
	private final LinkedHashMap<String, MachineNode> machinesMap = new LinkedHashMap<>();
	private final List<ExprNode> additionalFormulas = new ArrayList<>();

	public BProject(List<MachineNode> machineNodeList) {
		this(machineNodeList, new ArrayList<>());
	}
	public BProject(List<MachineNode> machineNodeList, List<ExprNode> additionalFormulas) {
		for (MachineNode node : machineNodeList) {
			machinesMap.put(node.getName(), node);
		}
		this.additionalFormulas.addAll(additionalFormulas);
	}

	public MachineNode getMainMachine() {
		return this.machinesMap.entrySet().iterator().next().getValue();
	}

	public MachineNode getMachineNode(String machineName) {
		if (machinesMap.containsKey(machineName)) {
			return machinesMap.get(machineName);
		} else {
			throw new RuntimeException("Unknown operation name: " + machineName);
		}
	}

	public List<ExprNode> getAdditionalFormulas() {
		return additionalFormulas;
	}
}
