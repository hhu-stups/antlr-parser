package de.prob.parser.antlr;

import de.prob.parser.ast.nodes.DeclarationNode;
import de.prob.parser.ast.nodes.Node;
import de.prob.parser.ast.nodes.expression.RecordFieldAccessNode;
import de.prob.parser.ast.nodes.expression.RecordNode;
import de.prob.parser.ast.nodes.expression.StructNode;
import de.prob.parser.ast.nodes.expression.ExprNode;
import de.prob.parser.ast.nodes.expression.ExpressionOperatorNode;
import de.prob.parser.ast.nodes.expression.ExpressionOperatorNode.ExpressionOperator;
import de.prob.parser.ast.nodes.expression.IdentifierExprNode;
import de.prob.parser.ast.nodes.expression.IfExpressionNode;
import de.prob.parser.ast.nodes.expression.LambdaNode;
import de.prob.parser.ast.nodes.expression.LetExpressionNode;
import de.prob.parser.ast.nodes.expression.NumberNode;
import de.prob.parser.ast.nodes.expression.QuantifiedExpressionNode;
import de.prob.parser.ast.nodes.expression.SetComprehensionNode;
import de.prob.parser.ast.nodes.expression.StringNode;
import de.prob.parser.ast.nodes.predicate.CastPredicateExpressionNode;
import de.prob.parser.ast.nodes.predicate.IfPredicateNode;
import de.prob.parser.ast.nodes.predicate.LetPredicateNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorNode.PredicateOperator;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorWithExprArgsNode;
import de.prob.parser.ast.nodes.predicate.PredicateOperatorWithExprArgsNode.PredOperatorExprArgs;
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
import de.prob.parser.ast.nodes.substitution.ListSubstitutionNode.ListOperator;
import de.prob.parser.ast.nodes.substitution.OperationCallSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.SkipSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.SubstitutionNode;
import de.prob.parser.ast.nodes.substitution.VarSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.WhileSubstitutionNode;
import files.BParser;
import files.BParser.AndOrListContext;
import files.BParser.BooleanValueContext;
import files.BParser.ExpressionContext;
import files.BParser.Expression_in_parContext;
import files.BParser.Expression_listContext;
import files.BParser.Identifier_or_function_or_recordContext;
import files.BParser.PredicateContext;
import files.BParser.Predicate_atomicContext;
import files.BParser.SubstitutionContext;
import files.BParser.Substitution_l1Context;
import files.BParserBaseVisitor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormulaASTCreator extends BParserBaseVisitor<Node> {

	private static final Map<Integer, ExpressionOperator> exprOperatorMap = new HashMap<>();

	private static void addExprOperator(Integer key, ExpressionOperator operator) {
		if (exprOperatorMap.containsKey(key)) {
			throw new RuntimeException("Duplicate key: " + operator);
		}
		exprOperatorMap.put(key, operator);
	}

	static {
		// arithmetic
		addExprOperator(BParser.PLUS, ExpressionOperator.PLUS);
		addExprOperator(BParser.MINUS, ExpressionOperator.MINUS);
		addExprOperator(BParser.POWER_OF, ExpressionOperator.POWER_OF);
		addExprOperator(BParser.MULT, ExpressionOperator.MULT);
		addExprOperator(BParser.DIVIDE, ExpressionOperator.DIVIDE);
		addExprOperator(BParser.MOD, ExpressionOperator.MOD);
		addExprOperator(BParser.NATURAL, ExpressionOperator.NATURAL);
		addExprOperator(BParser.NATURAL1, ExpressionOperator.NATURAL1);
		addExprOperator(BParser.INTEGER, ExpressionOperator.INTEGER);
		addExprOperator(BParser.NAT, ExpressionOperator.NAT);
		addExprOperator(BParser.NAT1, ExpressionOperator.NAT1);
		addExprOperator(BParser.INT, ExpressionOperator.INT);
		addExprOperator(BParser.MININT, ExpressionOperator.MININT);
		addExprOperator(BParser.MAXINT, ExpressionOperator.MAXINT);
		addExprOperator(BParser.INTERVAL, ExpressionOperator.INTERVAL);
		addExprOperator(BParser.PRED, ExpressionOperator.PRED);
		addExprOperator(BParser.SUCC, ExpressionOperator.SUCC);

		addExprOperator(BParser.TRUE, ExpressionOperator.TRUE);
		addExprOperator(BParser.FALSE, ExpressionOperator.FALSE);
		addExprOperator(BParser.BOOL, ExpressionOperator.BOOL);

		// sets
		addExprOperator(BParser.POW, ExpressionOperator.POW);
		addExprOperator(BParser.POW1, ExpressionOperator.POW1);
		addExprOperator(BParser.FIN, ExpressionOperator.FIN);
		addExprOperator(BParser.FIN1, ExpressionOperator.FIN1);
		addExprOperator(BParser.CARD, ExpressionOperator.CARD);
		addExprOperator(BParser.INTERSECTION, ExpressionOperator.INTERSECTION);
		addExprOperator(BParser.UNION, ExpressionOperator.UNION);
		addExprOperator(BParser.SET_SUBTRACTION, ExpressionOperator.SET_SUBTRACTION);
		addExprOperator(BParser.GENERALIZED_INTER, ExpressionOperator.GENERALIZED_INTER);
		addExprOperator(BParser.GENERALIZED_UNION, ExpressionOperator.GENERALIZED_UNION);

		addExprOperator(BParser.MIN, ExpressionOperator.MIN);
		addExprOperator(BParser.MAX, ExpressionOperator.MAX);

		// couples
		addExprOperator(BParser.MAPLET, ExpressionOperator.COUPLE);

		// relations
		addExprOperator(BParser.DOM, ExpressionOperator.DOMAIN);
		addExprOperator(BParser.RAN, ExpressionOperator.RANGE);
		addExprOperator(BParser.OVERWRITE_RELATION, ExpressionOperator.OVERWRITE_RELATION);
		addExprOperator(BParser.DIRECT_PRODUCT, ExpressionOperator.DIRECT_PRODUCT);
		addExprOperator(BParser.DOMAIN_RESTRICTION, ExpressionOperator.DOMAIN_RESTRICTION);
		addExprOperator(BParser.DOMAIN_SUBSTRACTION, ExpressionOperator.DOMAIN_SUBTRACTION);
		addExprOperator(BParser.RANGE_RESTRICTION, ExpressionOperator.RANGE_RESTRICTION);
		addExprOperator(BParser.RANGE_SUBSTRATION, ExpressionOperator.RANGE_SUBTRACTION);
		addExprOperator(BParser.TILDE, ExpressionOperator.INVERSE_RELATION);
		addExprOperator(BParser.SET_RELATION, ExpressionOperator.SET_RELATION);
		addExprOperator(BParser.TOTAL_SURJECTION_RELATION, ExpressionOperator.TOTAL_SURJECTION_RELATION);
		addExprOperator(BParser.SURJECTION_RELATION, ExpressionOperator.SURJECTION_RELATION);
		addExprOperator(BParser.ID, ExpressionOperator.ID);
		addExprOperator(BParser.SEMICOLON, ExpressionOperator.COMPOSITION);
		addExprOperator(BParser.DOUBLE_VERTICAL_BAR, ExpressionOperator.PARALLEL_PRODUCT);
		addExprOperator(BParser.CLOSURE, ExpressionOperator.CLOSURE);
		addExprOperator(BParser.CLOSURE1, ExpressionOperator.CLOSURE1);
		addExprOperator(BParser.ITERATE, ExpressionOperator.ITERATE);
		addExprOperator(BParser.PRJ1, ExpressionOperator.PRJ1);
		addExprOperator(BParser.PRJ2, ExpressionOperator.PRJ2);
		addExprOperator(BParser.FNC, ExpressionOperator.FNC);
		addExprOperator(BParser.REL, ExpressionOperator.REL);

		// functions
		addExprOperator(BParser.PARTIAL_BIJECTION, ExpressionOperator.PARTIAL_BIJECTION);
		addExprOperator(BParser.PARTIAL_FUNCTION, ExpressionOperator.PARTIAL_FUNCTION);
		addExprOperator(BParser.PARTIAL_INJECTION, ExpressionOperator.PARTIAL_INJECTION);
		addExprOperator(BParser.PARTIAL_SURJECTION, ExpressionOperator.PARTIAL_SURJECTION);
		addExprOperator(BParser.TOTAL_BIJECTION, ExpressionOperator.TOTAL_BIJECTION);
		addExprOperator(BParser.TOTAL_FUNCTION, ExpressionOperator.TOTAL_FUNCTION);
		addExprOperator(BParser.TOTAL_INJECTION, ExpressionOperator.TOTAL_INJECTION);
		addExprOperator(BParser.TOTAL_RELATION, ExpressionOperator.TOTAL_RELATION);
		addExprOperator(BParser.TOTAL_SURJECTION, ExpressionOperator.TOTAL_SURJECTION);

		// sequence operators
		addExprOperator(BParser.FIRST, ExpressionOperator.FIRST);
		addExprOperator(BParser.LAST, ExpressionOperator.LAST);
		addExprOperator(BParser.FRONT, ExpressionOperator.FRONT);
		addExprOperator(BParser.TAIL, ExpressionOperator.TAIL);
		addExprOperator(BParser.CONC, ExpressionOperator.CONC);
		addExprOperator(BParser.INSERT_FRONT, ExpressionOperator.INSERT_FRONT);
		addExprOperator(BParser.INSERT_TAIL, ExpressionOperator.INSERT_TAIL);
		addExprOperator(BParser.RESTRICT_FRONT, ExpressionOperator.RESTRICT_FRONT);
		addExprOperator(BParser.RESTRICT_TAIL, ExpressionOperator.RESTRICT_TAIL);
		addExprOperator(BParser.SEQ, ExpressionOperator.SEQ);
		addExprOperator(BParser.SEQ1, ExpressionOperator.SEQ1);
		addExprOperator(BParser.ISEQ, ExpressionOperator.ISEQ);
		addExprOperator(BParser.ISEQ1, ExpressionOperator.ISEQ1);
		addExprOperator(BParser.SIZE, ExpressionOperator.SIZE);
		addExprOperator(BParser.PERM, ExpressionOperator.PERM);
		addExprOperator(BParser.CONCAT, ExpressionOperator.CONCAT);
		addExprOperator(BParser.REV, ExpressionOperator.REV);

		addExprOperator(BParser.STRING, ExpressionOperator.STRING);
	}

	private static final Map<Integer, PredOperatorExprArgs> predicateBinOperatorMap = new HashMap<>();

	private static void addPredicateOperator(Integer key, PredOperatorExprArgs operator) {
		if (predicateBinOperatorMap.containsKey(key)) {
			throw new RuntimeException("Duplicate entry. key: " + key + ", operator: " + operator);
		}
		predicateBinOperatorMap.put(key, operator);
	}

	static {
		addPredicateOperator(BParser.EQUAL, PredOperatorExprArgs.EQUAL);
		addPredicateOperator(BParser.NOT_EQUAL, PredOperatorExprArgs.NOT_EQUAL);
		addPredicateOperator(BParser.ELEMENT_OF, PredOperatorExprArgs.ELEMENT_OF);
		addPredicateOperator(BParser.COLON, PredOperatorExprArgs.ELEMENT_OF);
		addPredicateOperator(BParser.LESS_EQUAL, PredOperatorExprArgs.LESS_EQUAL);
		addPredicateOperator(BParser.LESS, PredOperatorExprArgs.LESS);
		addPredicateOperator(BParser.GREATER_EQUAL, PredOperatorExprArgs.GREATER_EQUAL);
		addPredicateOperator(BParser.GREATER, PredOperatorExprArgs.GREATER);
		addPredicateOperator(BParser.NOT_BELONGING, PredOperatorExprArgs.NOT_BELONGING);
		addPredicateOperator(BParser.INCLUSION, PredOperatorExprArgs.INCLUSION);
		addPredicateOperator(BParser.STRICT_INCLUSION, PredOperatorExprArgs.STRICT_INCLUSION);
		addPredicateOperator(BParser.NON_INCLUSION, PredOperatorExprArgs.NON_INCLUSION);
		addPredicateOperator(BParser.STRICT_NON_INCLUSION, PredOperatorExprArgs.STRICT_NON_INCLUSION);
	}

	@Override
	public Node visitChildren(RuleNode node) {
		throw new RuntimeException("Not implemented: " + node.getClass().getSimpleName());
	}

	// Predicates

	public List<PredicateNode> createPredicateNodeList(PredicateNode... predicateArguments) {
		List<PredicateNode> list = new ArrayList<>();
		for (PredicateNode predicateNode : predicateArguments) {
			list.add(predicateNode);
		}
		return list;
	}

	@Override
	public PredicateNode visitAndOrList(AndOrListContext ctx) {
		List<Predicate_atomicContext> terms = ctx.terms;
		List<Token> operators = ctx.operators;

		PredicateOperator op = ctx.operators.get(ctx.operators.size() - 1).getType() == BParser.AND ? PredicateOperator.AND
				: PredicateOperator.OR;
		List<PredicateNode> args = new ArrayList<>();
		for (int i = operators.size() - 1; i >= 0; i--) {
			Predicate_atomicContext argContext = terms.get(operators.size() - 1 - i);
			PredicateNode arg = (PredicateNode) argContext.accept(this);
			args.add(arg);

			PredicateOperator newOp = ctx.operators.get(i).getType() == BParser.AND ? PredicateOperator.AND
					: PredicateOperator.OR;
			if (op != newOp) {
				PredicateNode temp = new PredicateOperatorNode(Util.createSourceCodePosition(ctx), newOp, args);
				args = new ArrayList<>();
				args.add(temp);
			}
		}
		Predicate_atomicContext lastContext = terms.get(terms.size()-1);
		PredicateNode last = (PredicateNode) lastContext.accept(this);
		args.add(last);
		return new PredicateOperatorNode(Util.createSourceCodePosition(ctx), op, args);
	}

	@Override
	public Node visitPredicateNot(BParser.PredicateNotContext ctx) {
		PredicateNode node = (PredicateNode) ctx.predicate().accept(this);
		return new PredicateOperatorNode(Util.createSourceCodePosition(ctx), PredicateOperator.NOT,
				createPredicateNodeList(node));
	}

	@Override
	public Node visitPredicateKeyword(BParser.PredicateKeywordContext ctx) {
		if (ctx.keyword == null) {
			throw new RuntimeException(ctx.keyword.getText());
		}
		int type = ctx.keyword.getType();
		PredicateOperator op = type == BParser.BTRUE ? PredicateOperator.TRUE : PredicateOperator.FALSE;
		List<PredicateNode> list = new ArrayList<>();
		return new PredicateOperatorNode(Util.createSourceCodePosition(ctx), op, list);
	}

	@Override
	public Node visitPredicateIdentifierCall(BParser.PredicateIdentifierCallContext ctx) {
		//TODO: Implement PredicateIdentifierCall
		return null;
	}

	@Override
	public Node visitImplication(BParser.ImplicationContext ctx) {
		PredicateNode left = (PredicateNode) ctx.left.accept(this);
		PredicateNode right = (PredicateNode) ctx.right.accept(this);
		List<PredicateNode> list = new ArrayList<>();
		list.add(left);
		list.add(right);
		return new PredicateOperatorNode(Util.createSourceCodePosition(ctx), PredicateOperator.IMPLIES, list);
	}

	@Override
	public Node visitPredicateBinPredicateOperator(BParser.PredicateBinPredicateOperatorContext ctx) {
		PredicateNode left = (PredicateNode) ctx.left.accept(this);
		PredicateNode right = (PredicateNode) ctx.right.accept(this);
		List<PredicateNode> list = new ArrayList<>();
		list.add(left);
		list.add(right);
		return new PredicateOperatorNode(Util.createSourceCodePosition(ctx), PredicateOperator.EQUIVALENCE, list);
	}

	@Override
	public Node visitPredicateP30Next(BParser.PredicateP30NextContext ctx) {
		return ctx.predicate_p40().accept(this);
	}

	@Override
	public Node visitPredicateP40Next(BParser.PredicateP40NextContext ctx) {
		return ctx.predicate_atomic().accept(this);
	}

	@Override
	public Node visitBinOperatorP160(BParser.BinOperatorP160Context ctx) {
		final ExprNode left = (ExprNode) ctx.left.accept(this);
		final ExprNode right = (ExprNode) ctx.right.accept(this);
		final int type = ctx.expressionOperatorP160().operator.getType();
		ExpressionOperator op = exprOperatorMap.get(type);
		if (op == null) {
			throw new RuntimeException("Not implemented: " + ctx.expressionOperatorP160().operator.getText());
		}
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), createExprNodeList(left, right), op);
	}

	@Override
	public Node visitQuantifiedPredicate(BParser.QuantifiedPredicateContext ctx) {
		final List<DeclarationNode> identifierList = new ArrayList<>();
		for (Token exprNode : ctx.quantified_variables_list().identifier_list().idents) {
			String name = exprNode.getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(exprNode), name,
					DeclarationNode.Kind.VARIABLE, null);
			identifierList.add(decl);
		}
		PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
		QuantifiedPredicateNode.QuantifiedPredicateOperator operator = BParser.FOR_ANY == ctx.operator.getType()
				? QuantifiedPredicateNode.QuantifiedPredicateOperator.UNIVERSAL_QUANTIFICATION
				: QuantifiedPredicateNode.QuantifiedPredicateOperator.EXISTENTIAL_QUANTIFICATION;
		return new QuantifiedPredicateNode(Util.createSourceCodePosition(ctx), identifierList, predicate, operator);
	}

	@Override
	public Node visitExpressionFunctionCall(BParser.ExpressionFunctionCallContext ctx) {
		List<ExprNode> list = new ArrayList<>();
		final ExprNode func = (ExprNode) ctx.expression().accept(this);
		list.add(func);
		for (Expression_in_parContext arg : ctx.expression_in_par()) {
			final ExprNode argNode = (ExprNode) arg.accept(this);
			list.add(argNode);
		}
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), list, ExpressionOperator.FUNCTION_CALL);
	}

	@Override
	public Node visitImageExpression(BParser.ImageExpressionContext ctx) {
		List<ExprNode> list = new ArrayList<>();
		final ExprNode func = (ExprNode) ctx.expression().accept(this);
		list.add(func);
		ExprNode arg = (ExprNode) ctx.expression_in_par().accept(this);
		list.add(arg);
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), list,
				ExpressionOperator.RELATIONAL_IMAGE);
	}

	@Override
	public Node visitReverseExpression(BParser.ReverseExpressionContext ctx) {
		List<ExprNode> list = new ArrayList<>();
		final ExprNode node = (ExprNode) ctx.expression().accept(this);
		list.add(node);
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), list,
				ExpressionOperator.INVERSE_RELATION);
	}

	@Override
	public Node visitUnaryMinus(BParser.UnaryMinusContext ctx) {
		ExprNode expr = (ExprNode) ctx.expression().accept(this);
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), createExprNodeList(expr),
				ExpressionOperator.UNARY_MINUS);
	}

	@Override
	public Node visitParenthesis(BParser.ParenthesisContext ctx) {
		return ctx.expression_in_par().accept(this);
	}

	@Override
	public Node visitEmptySet(BParser.EmptySetContext ctx) {
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), new ArrayList<>(),
				ExpressionOperator.SET_ENUMERATION);
	}

	@Override
	public Node visitEmptySequence(BParser.EmptySequenceContext ctx) {
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), new ArrayList<>(),
				ExpressionOperator.SEQ_ENUMERATION);
	}

	@Override
	public Node visitPredicateBinExpression(BParser.PredicateBinExpressionContext ctx) {
		ExprNode left = (ExprNode) ctx.left.accept(this);
		ExprNode right = (ExprNode) ctx.right.accept(this);

		int type = ctx.predicate_expression_operator().operator.getType();
		PredOperatorExprArgs op = predicateBinOperatorMap.get(type);
		if (op == null) {
			throw new RuntimeException();
		}
		return new PredicateOperatorWithExprArgsNode(Util.createSourceCodePosition(ctx), op,
				createExprNodeList(left, right));
	}

	// Expression

	@Override
	public Node visitExpressionKeyword(BParser.ExpressionKeywordContext ctx) {
		int type = ctx.expression_keyword().operator.getType();
		ExpressionOperator op = exprOperatorMap.get(type);
		if (op == null) {
			throw new RuntimeException(ctx.expression_keyword().operator.getText());
		}
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), op);
	}

	@Override
	public Node visitExpressionPrefixOperator(BParser.ExpressionPrefixOperatorContext ctx) {
		int type = ctx.expression_prefix_operator().operator.getType();
		ExpressionOperator op = exprOperatorMap.get(type);
		if (op == null) {
			throw new RuntimeException(ctx.expression_prefix_operator().operator.getText());
		}
		ExprNode argument = (ExprNode) ctx.expression_in_par().accept(this);
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), createExprNodeList(argument), op);
	}

	@Override
	public Node visitExpressionPrefixOperator2Args(BParser.ExpressionPrefixOperator2ArgsContext ctx) {
		int type = ctx.expression_prefix_operator_2_args().operator.getType();
		ExpressionOperator op = exprOperatorMap.get(type);
		if (op == null) {
			throw new RuntimeException(ctx.expression_prefix_operator_2_args().operator.getText());
		}
		ExprNode expr1 = (ExprNode) ctx.expr1.accept(this);
		ExprNode expr2 = (ExprNode) ctx.expr2.accept(this);
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), createExprNodeList(expr1, expr2), op);
	}

	@Override
	public Node visitPredicateParenthesis(BParser.PredicateParenthesisContext ctx) {
		return ctx.predicate().accept(this);
	}

	@Override
	public Node visitExpressionInParNext(BParser.ExpressionInParNextContext ctx) {
		return ctx.expression().accept(this);
	}

	@Override
	public ExprNode visitBinOperator(BParser.BinOperatorContext ctx) {
		ExprNode left = (ExprNode) ctx.left.accept(this);
		ExprNode right = (ExprNode) ctx.right.accept(this);
		final int type = ctx.operator.getType();
		final ExpressionOperator op = exprOperatorMap.get(type);
		if (op == null) {
			throw new RuntimeException("Not implemented operator: " + ctx.operator.getText());
		}
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), createExprNodeList(left, right), op);
	}

	@Override
	public Node visitExpressionBinOperatorP125(BParser.ExpressionBinOperatorP125Context ctx) {
		final ExprNode left = (ExprNode) ctx.left.accept(this);
		final ExprNode right = (ExprNode) ctx.right.accept(this);
		final int type = ctx.expression_bin_operator_p125().operator.getType();
		ExpressionOperator op = exprOperatorMap.get(type);
		if (op == null) {
			throw new RuntimeException("Not implemented: " + ctx.expression_bin_operator_p125().operator.getText());
		}
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), createExprNodeList(left, right), op);

	}

	@Override
	public Node visitCompositionOrParallelProduct(BParser.CompositionOrParallelProductContext ctx) {
		final ExprNode left = (ExprNode) ctx.left.accept(this);
		final ExprNode right = (ExprNode) ctx.right.accept(this);
		ExpressionOperator op = exprOperatorMap.get(ctx.operator.getType());
		if (op == null) {
			throw new RuntimeException("Not implemented: " + ctx.operator.getText());
		}
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), createExprNodeList(left, right), op);
	}

	@Override
	public Node visitLambdaExpression(BParser.LambdaExpressionContext ctx) {
		final List<DeclarationNode> identifierList = new ArrayList<>();
		for (Token exprNode : ctx.quantified_variables_list().identifier_list().idents) {
			String name = exprNode.getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(exprNode), name,
					DeclarationNode.Kind.VARIABLE, null);
			identifierList.add(decl);
		}
		PredicateNode predicateNode = (PredicateNode) ctx.predicate().accept(this);
		ExprNode exprNode = (ExprNode) ctx.expression_in_par().accept(this);
		return new LambdaNode(Util.createSourceCodePosition(ctx), identifierList, predicateNode, exprNode);
	}

	@Override
	public Node visitSetEnumeration(BParser.SetEnumerationContext ctx) {
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx),
				visitExpressionList(ctx.expression_list()), ExpressionOperator.SET_ENUMERATION);
	}

	@Override
	public Node visitSequenceEnumeration(BParser.SequenceEnumerationContext ctx) {
		return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx),
				visitExpressionList(ctx.expression_list()), ExpressionOperator.SEQ_ENUMERATION);
	}

	@Override
	public Node visitSetComprehension(BParser.SetComprehensionContext ctx) {
		final List<DeclarationNode> identifierList = new ArrayList<>();
		for (Token exprNode : ctx.identifier_list().idents) {
			String name = exprNode.getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(exprNode), name,
					DeclarationNode.Kind.VARIABLE, null);
			identifierList.add(decl);
		}
		PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
		return new SetComprehensionNode(Util.createSourceCodePosition(ctx), identifierList, predicate);
	}

	@Override
	public Node visitQuantifiedExpression(BParser.QuantifiedExpressionContext ctx) {
		QuantifiedExpressionNode.QuantifiedExpressionOperator operator = null;
		switch(ctx.operator.getType()) {
			case BParser.SIGMA:
				operator = QuantifiedExpressionNode.QuantifiedExpressionOperator.SIGMA;
				break;
			case BParser.PI:
				operator = QuantifiedExpressionNode.QuantifiedExpressionOperator.PI;
				break;
			case BParser.QUANTIFIED_INTER:
				operator = QuantifiedExpressionNode.QuantifiedExpressionOperator.QUANTIFIED_INTER;
				break;
			case BParser.QUANTIFIED_UNION:
				operator = QuantifiedExpressionNode.QuantifiedExpressionOperator.QUANTIFIED_UNION;
				break;
		}
		final List<DeclarationNode> identifierList = new ArrayList<>();
		for (Token exprNode : ctx.quantified_variables_list().identifier_list().idents) {
			String name = exprNode.getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(exprNode), name,
					DeclarationNode.Kind.VARIABLE, null);
			identifierList.add(decl);
		}
		PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
		ExprNode expression = (ExprNode) ctx.expression_in_par().accept(this);
		return new QuantifiedExpressionNode(Util.createSourceCodePosition(ctx), operator, identifierList, predicate, expression);
	}

	@Override
	public Node visitNumber(BParser.NumberContext ctx) {
		BigInteger value = new BigInteger(ctx.Number().getText());
		return new NumberNode(Util.createSourceCodePosition(ctx), value);
	}

	@Override
	public Node visitBooleanValue(BParser.BooleanValueContext ctx) {
		if (ctx.value.getText().equals("TRUE")) {
			return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), ExpressionOperator.TRUE);
		} else if (ctx.value.getText().equals("FALSE")) {
			return new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), ExpressionOperator.FALSE);
		}
		return notReachable(ctx);
	}

	@Override
	public Node visitBoolCastExpression(BParser.BoolCastExpressionContext ctx) {
		return new CastPredicateExpressionNode(Util.createSourceCodePosition(ctx),
				(PredicateNode) ctx.predicate().accept(this));
	}

	@Override
	public Node visitExpressionIdentifier(BParser.ExpressionIdentifierContext ctx) {
		return new IdentifierExprNode(Util.createSourceCodePosition(ctx), ctx.getText(), false);
	}

	@Override
	public Node visitPrimedIdentifierExpression(BParser.PrimedIdentifierExpressionContext ctx) {
		return new IdentifierExprNode(Util.createSourceCodePosition(ctx), ctx.getText().substring(0, ctx.getText().length() - 2), true);
	}

	// Substitution
	@Override
	public SubstitutionNode visitAssignSubstitution(BParser.AssignSubstitutionContext ctx) {
		List<ExprNode> leftList = new ArrayList<>();
		for (Identifier_or_function_or_recordContext left : ctx.identifier_or_function_or_record()) {
			ExprNode leftNode = (ExprNode) left.accept(this);
			leftList.add(leftNode);
		}

		List<ExprNode> exprList = visitExpressionList(ctx.expression_list());

		return new AssignSubstitutionNode(Util.createSourceCodePosition(ctx), leftList, exprList);
	}

	@Override
	public Node visitSubstitutionIdentifierCall(BParser.SubstitutionIdentifierCallContext ctx) {
		List<String> names = new ArrayList<>();
		for (TerminalNode tNode : ctx.composed_identifier().IDENTIFIER()) {
			names.add(tNode.getText());
		}
		List<ExprNode> arguments = ctx.expression_list() == null ? new ArrayList<>()
				: visitExpressionList(ctx.expression_list());
		return new OperationCallSubstitutionNode(Util.createSourceCodePosition(ctx), names, arguments);
	}

	@Override
	public Node visitSubstitutionOperationCall(BParser.SubstitutionOperationCallContext ctx) {
		List<String> names = new ArrayList<>();
		for (TerminalNode tNode : ctx.composed_identifier().IDENTIFIER()) {
			names.add(tNode.getText());
		}
		List<ExprNode> arguments = ctx.expression_list() == null ? new ArrayList<>()
				: visitExpressionList(ctx.expression_list());

		List<ExprNode> output = new ArrayList<>();
		for (Token exprNode : ctx.identifier_list().idents) {
			String name = exprNode.getText();
			IdentifierExprNode identifierExprNode = new IdentifierExprNode(Util.createSourceCodePosition(exprNode),
					name, false);
			output.add(identifierExprNode);
		}

		return new OperationCallSubstitutionNode(Util.createSourceCodePosition(ctx), names, arguments, output);
	}

	@Override
	public Node visitSubstitutionBlock(BParser.SubstitutionBlockContext ctx) {
		return ctx.substitution().accept(this);
	}

	@Override
	public Node visitSubstitutionSkip(BParser.SubstitutionSkipContext ctx) {
		return new SkipSubstitutionNode(Util.createSourceCodePosition(ctx));
	}

	@Override
	public Node visitSubstitutionList(BParser.SubstitutionListContext ctx) {

		List<Token> operators = ctx.operators;
		int token = operators.get(0).getType();
		// check that all tokens are of the same type
		for (int i = 1; i < operators.size(); i++) {
			if (token != operators.get(i).getType()) {
				throw new RuntimeException("Error in Line: " + String.valueOf(ctx.operators.get(0).getTokenSource().getLine()));
			}
		}

		List<SubstitutionNode> result = new ArrayList<>();
		for (Substitution_l1Context substitutionContext : ctx.substitution_l1()) {
			SubstitutionNode sub = (SubstitutionNode) substitutionContext.accept(this);
			result.add(sub);
		}

		ListOperator operator = null;
		if (token == BParser.SEMICOLON) {
			operator = ListOperator.Sequential;
		} else {
			operator = ListOperator.Parallel;
		}

		return new ListSubstitutionNode(Util.createSourceCodePosition(ctx), operator, result);
	}

	@Override
	public Node visitIfSubstitution(BParser.IfSubstitutionContext ctx) {
		List<PredicateNode> predList = new ArrayList<>();
		List<SubstitutionNode> subList = new ArrayList<>();

		PredicateNode firstPred = (PredicateNode) ctx.pred.accept(this);
		predList.add(firstPred);
		SubstitutionNode firstSub = (SubstitutionNode) ctx.thenSub.accept(this);
		subList.add(firstSub);

		for (PredicateContext predCtx : ctx.elsifPred) {
			PredicateNode pred = (PredicateNode) predCtx.accept(this);
			predList.add(pred);
		}

		for (SubstitutionContext subCtx : ctx.elsifSub) {
			SubstitutionNode sub = (SubstitutionNode) subCtx.accept(this);
			subList.add(sub);
		}

		SubstitutionNode elseSubstitution = null;
		if (ctx.elseSub != null) {
			elseSubstitution = (SubstitutionNode) ctx.elseSub.accept(this);
		}
		return new IfOrSelectSubstitutionsNode(Util.createSourceCodePosition(ctx),
				IfOrSelectSubstitutionsNode.Operator.IF, predList, subList, elseSubstitution);
	}

	@Override
	public Node visitIfExpression(BParser.IfExpressionContext ctx) {
		PredicateNode condition = (PredicateNode) ctx.predicate().accept(this);
		ExprNode thenExpr = (ExprNode) ctx.expr1.accept(this);
		ExprNode elseExpr = (ExprNode) ctx.expr2.accept(this);
		return new IfExpressionNode(Util.createSourceCodePosition(ctx), condition, thenExpr, elseExpr);
	}

	@Override
	public Node visitPredicateIf(BParser.PredicateIfContext ctx) {
		PredicateNode condition = (PredicateNode) ctx.conditionPred.accept(this);
		PredicateNode thenPred = (PredicateNode) ctx.thenPred.accept(this);
		PredicateNode elsePred = (PredicateNode) ctx.elsePred.accept(this);
		return new IfPredicateNode(Util.createSourceCodePosition(ctx), condition, thenPred, elsePred);
	}

	@Override
	public Node visitWhileSubstitution(BParser.WhileSubstitutionContext ctx) {
		PredicateNode condition = (PredicateNode) ctx.condition.accept(this);
		SubstitutionNode body = (SubstitutionNode) ctx.substitution().accept(this);
		PredicateNode invariant = (PredicateNode) ctx.invariant.accept(this);
		ExprNode variant = (ExprNode) ctx.variant.accept(this);
		return new WhileSubstitutionNode(Util.createSourceCodePosition(ctx), condition, body, invariant, variant);
	}

	@Override
	public Node visitVarSubstitution(BParser.VarSubstitutionContext ctx) {
		final List<DeclarationNode> identifierList = new ArrayList<>();
		for (Token exprNode : ctx.identifier_list().idents) {
			String name = exprNode.getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(exprNode), name,
					DeclarationNode.Kind.SUBSTITUION_IDENTIFIER, null);
			identifierList.add(decl);
		}
		SubstitutionNode sub = (SubstitutionNode) ctx.substitution().accept(this);
		return new VarSubstitutionNode(Util.createSourceCodePosition(ctx), identifierList, sub);
	}

	@Override
	public Node visitLetSubstitution(BParser.LetSubstitutionContext ctx) {
		final List<DeclarationNode> identifierList = new ArrayList<>();
		for (Token exprNode : ctx.identifier_list().idents) {
			String name = exprNode.getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(exprNode), name,
					DeclarationNode.Kind.SUBSTITUION_IDENTIFIER, null);
			identifierList.add(decl);
		}
		PredicateNode pred = (PredicateNode) ctx.predicate().accept(this);
		SubstitutionNode sub = (SubstitutionNode) ctx.substitution().accept(this);
		return new LetSubstitutionNode(Util.createSourceCodePosition(ctx), identifierList, pred, sub);
	}

	@Override
	public Node visitLetExpression(BParser.LetExpressionContext ctx) {
		final List<DeclarationNode> identifierList = new ArrayList<>();
		for (Token exprNode : ctx.identifier_list().idents) {
			String name = exprNode.getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(exprNode), name,
					DeclarationNode.Kind.SUBSTITUION_IDENTIFIER, null);
			identifierList.add(decl);
		}
		PredicateNode pred = (PredicateNode) ctx.predicate().accept(this);
		ExprNode sub = (ExprNode) ctx.expression_in_par().accept(this);
		return new LetExpressionNode(Util.createSourceCodePosition(ctx), identifierList, pred, sub);
	}

	@Override
	public Node visitPredicateLet(BParser.PredicateLetContext ctx) {
		final List<DeclarationNode> identifierList = new ArrayList<>();
		for (Token exprNode : ctx.identifier_list().idents) {
			String name = exprNode.getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(exprNode), name,
					DeclarationNode.Kind.SUBSTITUION_IDENTIFIER, null);
			identifierList.add(decl);
		}
		PredicateNode pred = (PredicateNode) ctx.pred1.accept(this);
		PredicateNode sub = (PredicateNode) ctx.pred2.accept(this);
		return new LetPredicateNode(Util.createSourceCodePosition(ctx), identifierList, pred, sub);
	}

	@Override
	public Node visitSelectSubstitution(BParser.SelectSubstitutionContext ctx) {
		List<PredicateNode> predList = new ArrayList<>();
		List<SubstitutionNode> subList = new ArrayList<>();

		PredicateNode firstPred = (PredicateNode) ctx.pred.accept(this);
		predList.add(firstPred);
		SubstitutionNode firstSub = (SubstitutionNode) ctx.sub.accept(this);
		subList.add(firstSub);

		for (PredicateContext predCtx : ctx.when_pred) {
			PredicateNode pred = (PredicateNode) predCtx.accept(this);
			predList.add(pred);
		}

		for (SubstitutionContext subCtx : ctx.when_sub) {
			SubstitutionNode sub = (SubstitutionNode) subCtx.accept(this);
			subList.add(sub);
		}

		SubstitutionNode elseSubstitution = null;
		if (ctx.else_sub != null) {
			elseSubstitution = (SubstitutionNode) ctx.else_sub.accept(this);
		}
		return new IfOrSelectSubstitutionsNode(Util.createSourceCodePosition(ctx),
				IfOrSelectSubstitutionsNode.Operator.SELECT, predList, subList, elseSubstitution);
	}

	@Override
	public Node visitAnySubstitution(BParser.AnySubstitutionContext ctx) {
		PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
		SubstitutionNode substitution = (SubstitutionNode) ctx.substitution().accept(this);
		List<DeclarationNode> identifierList = new ArrayList<>();

		for (Token node : ctx.identifier_list().idents) {
			String name = node.getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(node), name,
					DeclarationNode.Kind.SUBSTITUION_IDENTIFIER, null);
			identifierList.add(decl);
		}
		return new AnySubstitutionNode(Util.createSourceCodePosition(ctx), identifierList, predicate, substitution);
	}

	@Override
	public Node visitBecomesElementOfSubstitution(BParser.BecomesElementOfSubstitutionContext ctx) {
		List<IdentifierExprNode> leftList = new ArrayList<>();
		for (Token left : ctx.identifier_list().idents) {
			String name = left.getText();
			IdentifierExprNode identifierExprNode = new IdentifierExprNode(Util.createSourceCodePosition(left), name, false);
			leftList.add(identifierExprNode);
		}

		ExprNode expression = (ExprNode) ctx.expression().accept(this);
		return new BecomesElementOfSubstitutionNode(Util.createSourceCodePosition(ctx), leftList, expression);
	}

	@Override
	public Node visitBecomesSuchThatSubstitution(BParser.BecomesSuchThatSubstitutionContext ctx) {
		List<IdentifierExprNode> leftList = new ArrayList<>();
		for (Token left : ctx.identifier_list().idents) {
			String name = left.getText();
			IdentifierExprNode identifierExprNode = new IdentifierExprNode(Util.createSourceCodePosition(left), name, false);
			leftList.add(identifierExprNode);
		}
		PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
		return new BecomesSuchThatSubstitutionNode(Util.createSourceCodePosition(ctx), leftList, predicate);
	}

	@Override
	public ExprNode visitAssignSingleIdentifier(BParser.AssignSingleIdentifierContext ctx) {
		return new IdentifierExprNode(Util.createSourceCodePosition(ctx), ctx.getText(), false);
	}

	@Override
	public ExprNode visitAssignFunctionIdentifier(BParser.AssignFunctionIdentifierContext ctx) {

		final ExprNode func = new IdentifierExprNode(Util.createSourceCodePosition(ctx), ctx.IDENTIFIER().getText(), false);
		ExprNode resultNode = func;

		for(int i = 0; i < ctx.argument_lists.size(); i++) {
			List<ExprNode> list = new ArrayList<>();
			list.add(resultNode);
			Expression_listContext listCtx = ctx.argument_lists.get(i);
			List<ExprNode> arguments = listCtx == null ? new ArrayList<>()
					: visitExpressionList(listCtx);
			list.addAll(arguments);
			resultNode = new ExpressionOperatorNode(Util.createSourceCodePosition(ctx), list, ExpressionOperator.FUNCTION_CALL);

		}
		return resultNode;
	}

	@Override
	public Node visitSubstitutionNextL1(BParser.SubstitutionNextL1Context ctx) {
		return ctx.substitution_l1().accept(this);
	}

	// Util
	private List<ExprNode> visitExpressionList(Expression_listContext expression_list) {
		List<ExprNode> list = new ArrayList<>();
		for (ExpressionContext eCtx : expression_list.exprs) {
			ExprNode expr = (ExprNode) eCtx.accept(this);
			list.add(expr);
		}
		return list;
	}

	public static List<ExprNode> createExprNodeList(ExprNode... nodes) {
		List<ExprNode> list = new ArrayList<>();
		for (ExprNode exprNode : nodes) {
			list.add(exprNode);
		}
		return list;
	}

	@Override
	public Node visitString(BParser.StringContext ctx) {
		return new StringNode(Util.createSourceCodePosition(ctx), ctx.StringLiteral().getText());
	}

	private Node notReachable(BooleanValueContext ctx) {
		return null;
	}

	@Override
	public Node visitConditionSubstitution(BParser.ConditionSubstitutionContext ctx) {
		PredicateNode predicate = (PredicateNode) ctx.predicate().accept(this);
		SubstitutionNode substitution = (SubstitutionNode) ctx.substitution().accept(this);
		if("PRE".equals(ctx.keyword.getText())) {
			return new ConditionSubstitutionNode(Util.createSourceCodePosition(ctx), ConditionSubstitutionNode.Kind.PRECONDITION, predicate, substitution);
		} else {
			return new ConditionSubstitutionNode(Util.createSourceCodePosition(ctx), ConditionSubstitutionNode.Kind.ASSERT, predicate, substitution);
		}
	}

	@Override
	public Node visitChoiceSubstitution(BParser.ChoiceSubstitutionContext ctx) {
		List<SubstitutionNode> substitutions = new ArrayList<>();
		for (SubstitutionContext sCtx : ctx.substitution()) {
			substitutions.add((SubstitutionNode) sCtx.accept(this));
		}
		return new ChoiceSubstitutionNode(Util.createSourceCodePosition(ctx), substitutions);
	}

	@Override
	public Node visitRecord(BParser.RecordContext ctx) {
		List<DeclarationNode> declarations = new ArrayList<>();
		List<ExprNode> expressions = new ArrayList<>();
		for(BParser.Rec_entryContext entry : ctx.entries) {
			String name = entry.identifier().getText();
			DeclarationNode decl = new DeclarationNode(Util.createSourceCodePosition(entry.getStart()), name,
					DeclarationNode.Kind.VARIABLE, null);
			declarations.add(decl);
			expressions.add((ExprNode) entry.expression_in_par().accept(this));
		}
		if(ctx.operator.getType() == BParser.STRUCT) {
			return new StructNode(Util.createSourceCodePosition(ctx), declarations, expressions);
		}
		return new RecordNode(Util.createSourceCodePosition(ctx), declarations, expressions);
	}

	@Override
	public Node visitRecordFieldAccess(BParser.RecordFieldAccessContext ctx) {
		ExprNode expression = (ExprNode) ctx.expression().accept(this);
		String name = ctx.identifier().getText();
		DeclarationNode identifier = new DeclarationNode(Util.createSourceCodePosition(ctx.identifier().getStart()), name,
				DeclarationNode.Kind.VARIABLE, null);
		return new RecordFieldAccessNode(Util.createSourceCodePosition(ctx), expression, identifier);
	}

	@Override
	public Node visitIdentifier(BParser.IdentifierContext ctx) {
		return new IdentifierExprNode(Util.createSourceCodePosition(ctx), ctx.getText(), false);
	}

	@Override
	public Node visitAssignRecordIdentifier(BParser.AssignRecordIdentifierContext ctx) {
		final ExprNode record = new IdentifierExprNode(Util.createSourceCodePosition(ctx), ctx.name.getText(), false);
		RecordFieldAccessNode result = null;
		for(int i = 0; i < ctx.attributes.size(); i++) {
			String name = ctx.attributes.get(i).getText();
			DeclarationNode identifier = new DeclarationNode(Util.createSourceCodePosition(ctx.attributes.get(i)), name,
					DeclarationNode.Kind.VARIABLE, null);
			if(i == 0) {
				result = new RecordFieldAccessNode(Util.createSourceCodePosition(ctx), record, identifier);
			} else {
				result = new RecordFieldAccessNode(Util.createSourceCodePosition(ctx), result, identifier);
			}
		}
		return result;
	}
}
