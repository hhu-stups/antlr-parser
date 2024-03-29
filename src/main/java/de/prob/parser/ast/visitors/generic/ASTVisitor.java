package de.prob.parser.ast.visitors.generic;

import de.prob.parser.ast.nodes.expression.RealNumberNode;
import de.prob.parser.ast.nodes.expression.RecordFieldAccessNode;
import de.prob.parser.ast.nodes.expression.RecordNode;
import de.prob.parser.ast.nodes.expression.StructNode;
import de.prob.parser.ast.nodes.expression.ExprNode;
import de.prob.parser.ast.nodes.expression.ExpressionOperatorNode;
import de.prob.parser.ast.nodes.expression.IdentifierExprNode;
import de.prob.parser.ast.nodes.expression.IfExpressionNode;
import de.prob.parser.ast.nodes.expression.LambdaNode;
import de.prob.parser.ast.nodes.expression.LetExpressionNode;
import de.prob.parser.ast.nodes.expression.NumberNode;
import de.prob.parser.ast.nodes.expression.QuantifiedExpressionNode;
import de.prob.parser.ast.nodes.expression.SetComprehensionNode;
import de.prob.parser.ast.nodes.expression.StringNode;
import de.prob.parser.ast.nodes.predicate.CastPredicateExpressionNode;
import de.prob.parser.ast.nodes.predicate.IdentifierPredicateNode;
import de.prob.parser.ast.nodes.predicate.IfPredicateNode;
import de.prob.parser.ast.nodes.predicate.LetPredicateNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorWithExprArgsNode;
import de.prob.parser.ast.nodes.predicate.QuantifiedPredicateNode;
import de.prob.parser.ast.nodes.substitution.AnySubstitutionNode;
import de.prob.parser.ast.nodes.substitution.AssignSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.BecomesElementOfSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.BecomesSuchThatSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.ChoiceSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.ConditionSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.IfOrSelectSubstitutionsNode;
import de.prob.parser.ast.nodes.substitution.LetSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.ListSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.OperationCallSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.SkipSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.SubstitutionNode;
import de.prob.parser.ast.nodes.substitution.VarSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.WhileSubstitutionNode;

public class ASTVisitor implements ExpressionVisitor, SubstitutionVisitor, PredicateVisitor {

	@Override
	public void visitIdentifierPredicateNode(IdentifierPredicateNode node) {
		// no children
	}

	@Override
	public void visitPredicateOperatorNode(PredicateOperatorNode node) {
		for (PredicateNode arg : node.getPredicateArguments()) {
			visitPredicateNode(arg);
		}
	}

	@Override
	public void visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node) {
		for (ExprNode arg : node.getExpressionNodes()) {
			visitExprNode(arg);
		}
	}

	@Override
	public void visitQuantifiedPredicateNode(QuantifiedPredicateNode node) {
		visitPredicateNode(node.getPredicateNode());
	}

	@Override
	public void visitAssignSubstitutionNode(AssignSubstitutionNode node) {
		for (ExprNode arg : node.getLeftSide()) {
			visitExprNode(arg);
		}

		for (ExprNode arg : node.getRightSide()) {
			visitExprNode(arg);
		}
	}

	@Override
	public void visitSkipSubstitutionNode(SkipSubstitutionNode node) {
		// no children
	}

	@Override
	public void visitConditionSubstitutionNode(ConditionSubstitutionNode node) {
		visitPredicateNode(node.getCondition());
		visitSubstitutionNode(node.getSubstitution());
	}

	@Override
	public void visitAnySubstitution(AnySubstitutionNode node) {
		visitPredicateNode(node.getWherePredicate());
		visitSubstitutionNode(node.getThenSubstitution());
	}

	@Override
	public void visitLetSubstitution(LetSubstitutionNode node) {
		visitPredicateNode(node.getPredicate());
		visitSubstitutionNode(node.getBody());
	}

	@Override
	public void visitLetExpressionNode(LetExpressionNode node) {
		visitPredicateNode(node.getPredicate());
		visitExprNode(node.getExpression());
	}

	@Override
	public void visitLetPredicateNode(LetPredicateNode node) {
		visitPredicateNode(node.getWherePredicate());
		visitPredicateNode(node.getPredicate());
	}

	@Override
	public void visitIfOrSelectSubstitutionsNode(IfOrSelectSubstitutionsNode node) {
		for (PredicateNode pred : node.getConditions()) {
			visitPredicateNode(pred);
		}
		for (SubstitutionNode arg : node.getSubstitutions()) {
			visitSubstitutionNode(arg);
		}
		if (node.getElseSubstitution() != null) {
			visitSubstitutionNode(node.getElseSubstitution());
		}
	}

	@Override
	public void visitIfExpressionNode(IfExpressionNode node) {
		visitPredicateNode(node.getCondition());
		visitExprNode(node.getThenExpression());
		visitExprNode(node.getElseExpression());
	}

	@Override
	public void visitStringNode(StringNode node) {
		//no children
	}

	@Override
	public void visitIfPredicateNode(IfPredicateNode node) {
		visitPredicateNode(node.getCondition());
		visitPredicateNode(node.getThenPredicate());
		visitPredicateNode(node.getElsePredicate());
	}

	@Override
	public void visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node) {
		// no children
	}

	@Override
	public void visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node) {
		// no children
	}

	@Override
	public void visitExprOperatorNode(ExpressionOperatorNode node) {
		for (ExprNode arg : node.getExpressionNodes()) {
			visitExprNode(arg);
		}

	}

	@Override
	public void visitIdentifierExprNode(IdentifierExprNode node) {
		// no children

	}

	@Override
	public void visitCastPredicateExpressionNode(CastPredicateExpressionNode node) {
		visitPredicateNode(node.getPredicate());
	}

	@Override
	public void visitNumberNode(NumberNode node) {
		// no children
	}

	@Override
	public void visitLambdaNode(LambdaNode node) {
		visitPredicateNode(node.getPredicate());
		visitExprNode(node.getExpression());
	}

	@Override
	public void visitQuantifiedExpressionNode(QuantifiedExpressionNode node) {
		visitPredicateNode(node.getPredicateNode());
		visitExprNode(node.getExpressionNode());
	}

	@Override
	public void visitSetComprehensionNode(SetComprehensionNode node) {
		visitPredicateNode(node.getPredicateNode());
	}

	@Override
	public void visitListSubstitutionNode(ListSubstitutionNode node) {
		for (SubstitutionNode arg : node.getSubstitutions()) {
			visitSubstitutionNode(arg);
		}
	}

	@Override
	public void visitSubstitutionIdentifierCallNode(OperationCallSubstitutionNode node) {
		for (ExprNode arg : node.getArguments()) {
			visitExprNode(arg);
		}
	}

	@Override
	public void visitWhileSubstitutionNode(WhileSubstitutionNode node) {
		visitPredicateNode(node.getCondition());
		visitSubstitutionNode(node.getBody());
		visitPredicateNode(node.getInvariant());
		visitExprNode(node.getVariant());

	}

	@Override
	public void visitVarSubstitutionNode(VarSubstitutionNode node) {
		visitSubstitutionNode(node.getBody());
	}

	@Override
	public void visitChoiceSubstitutionNode(ChoiceSubstitutionNode node) {
		for(SubstitutionNode substitution : node.getSubstitutions()) {
			visitSubstitutionNode(substitution);
		}
	}

	@Override
	public void visitRecordNode(RecordNode node) {
		node.getExpressions().forEach(this::visitExprNode);
	}

	@Override
	public void visitStructNode(StructNode node) {
		node.getExpressions().forEach(this::visitExprNode);
	}

	@Override
	public void visitRecordFieldAccessNode(RecordFieldAccessNode node) {
		visitExprNode(node.getRecord());
	}

	@Override
	public void visitRealNumberNode(RealNumberNode node) {
		// no children
	}
}
