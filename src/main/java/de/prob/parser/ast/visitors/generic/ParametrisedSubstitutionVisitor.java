package de.prob.parser.ast.visitors.generic;

import de.prob.parser.ast.nodes.substitution.AnySubstitutionNode;
import de.prob.parser.ast.nodes.substitution.AssignSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.BecomesElementOfSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.BecomesSuchThatSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.ChoiceSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.ConditionSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.IfOrSelectSubstitutionsNode;
import de.prob.parser.ast.nodes.substitution.LetSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.ListSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.SkipSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.OperationCallSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.SubstitutionNode;
import de.prob.parser.ast.nodes.substitution.VarSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.WhileSubstitutionNode;

public interface ParametrisedSubstitutionVisitor<R, P> {

	default R visitSubstitutionNode(SubstitutionNode node, P expected) {
		if (node instanceof IfOrSelectSubstitutionsNode) {
			return visitIfOrSelectSubstitutionsNode((IfOrSelectSubstitutionsNode) node, expected);
		} else if (node instanceof AnySubstitutionNode) {
			return visitAnySubstitution((AnySubstitutionNode) node, expected);
		} else if (node instanceof LetSubstitutionNode) {
			return visitLetSubstitution((LetSubstitutionNode) node, expected);
		} else if (node instanceof BecomesSuchThatSubstitutionNode) {
			return visitBecomesSuchThatSubstitutionNode((BecomesSuchThatSubstitutionNode) node, expected);
		} else if (node instanceof BecomesElementOfSubstitutionNode) {
			return visitBecomesElementOfSubstitutionNode((BecomesElementOfSubstitutionNode) node, expected);
		} else if (node instanceof ConditionSubstitutionNode) {
			return visitConditionSubstitutionNode((ConditionSubstitutionNode) node, expected);
		} else if (node instanceof SkipSubstitutionNode) {
			return visitSkipSubstitutionNode((SkipSubstitutionNode) node, expected);
		} else if (node instanceof AssignSubstitutionNode) {
			return visitAssignSubstitutionNode((AssignSubstitutionNode) node, expected);
		} else if (node instanceof ListSubstitutionNode) {
			return visitListSubstitutionNode((ListSubstitutionNode) node, expected);
		} else if (node instanceof OperationCallSubstitutionNode) {
			return visitSubstitutionIdentifierCallNode((OperationCallSubstitutionNode) node, expected);
		}else if (node instanceof WhileSubstitutionNode) {
			return visitWhileSubstitutionNode((WhileSubstitutionNode) node, expected);
		}else if (node instanceof VarSubstitutionNode) {
			return visitVarSubstitutionNode((VarSubstitutionNode) node, expected);
		}else if(node instanceof ChoiceSubstitutionNode) {
			return visitChoiceSubstitutionNode((ChoiceSubstitutionNode) node, expected);
		}
		throw new AssertionError(node.getClass());
	}

	R visitVarSubstitutionNode(VarSubstitutionNode node, P expected);

	R visitWhileSubstitutionNode(WhileSubstitutionNode node, P expected);

	R visitListSubstitutionNode(ListSubstitutionNode node, P expected);

	R visitIfOrSelectSubstitutionsNode(IfOrSelectSubstitutionsNode node, P expected);

	R visitAssignSubstitutionNode(AssignSubstitutionNode node, P expected);

	R visitSkipSubstitutionNode(SkipSubstitutionNode node, P expected);

	R visitConditionSubstitutionNode(ConditionSubstitutionNode node, P expected);

	R visitAnySubstitution(AnySubstitutionNode node, P expected);

	R visitLetSubstitution(LetSubstitutionNode node, P expected);

	R visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, P expected);

	R visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, P expected);

	R visitSubstitutionIdentifierCallNode(OperationCallSubstitutionNode node, P expected);

	R visitChoiceSubstitutionNode(ChoiceSubstitutionNode node, P expected);

}
