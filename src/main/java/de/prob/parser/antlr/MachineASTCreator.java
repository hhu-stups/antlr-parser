package de.prob.parser.antlr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.parser.ast.SourceCodePosition;
import de.prob.parser.ast.nodes.DeclarationNode;
import de.prob.parser.ast.nodes.DefinitionNode;
import de.prob.parser.ast.nodes.EnumeratedSetDeclarationNode;
import de.prob.parser.ast.nodes.FreetypeBaseElementNode;
import de.prob.parser.ast.nodes.FreetypeConstructorNode;
import de.prob.parser.ast.nodes.FreetypeDeclarationNode;
import de.prob.parser.ast.nodes.FreetypeElementNode;
import de.prob.parser.ast.nodes.MachineNode;
import de.prob.parser.ast.nodes.MachineReferenceNode;
import de.prob.parser.ast.nodes.Node;
import de.prob.parser.ast.nodes.OperationNode;
import de.prob.parser.ast.nodes.OperationReferenceNode;
import de.prob.parser.ast.nodes.expression.ExprNode;
import de.prob.parser.ast.nodes.expression.IdentifierExprNode;
import de.prob.parser.ast.nodes.predicate.PredicateNode;
import de.prob.parser.ast.nodes.substitution.AssignSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.SubstitutionNode;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import files.BParser;
import files.BParser.DeclarationClauseContext;
import files.BParser.FormulaContext;
import files.BParser.FormulaExpressionContext;
import files.BParser.FormulaPredicateContext;
import files.BParser.FormulaSubstitutionContext;
import files.BParser.Machine_instantiationContext;
import files.BParser.StartContext;
import files.BParserBaseVisitor;

public class MachineASTCreator {
	private final MachineNode machineNode;

	public static MachineNode createMachineAST(StartContext startContext) {
		MachineASTCreator machineASTCreator = new MachineASTCreator(startContext);
		return machineASTCreator.getMachineNode();
	}

	public static ExprNode createExpressionAST(BParser.ExpressionContext context) {
		FormulaASTCreator formulaASTCreator = new FormulaASTCreator();
		return (ExprNode) context.accept(formulaASTCreator);
	}

	public static PredicateNode createPredicateAST(BParser.PredicateContext context) {
		FormulaASTCreator formulaASTCreator = new FormulaASTCreator();
		return (PredicateNode) context.accept(formulaASTCreator);
	}

	private MachineNode getMachineNode() {
		return this.machineNode;
	}

	private MachineASTCreator(StartContext startContext) {
		this.machineNode = new MachineNode(Util.createSourceCodePosition(startContext));
		new MachineConstructor(startContext);
	}

	class MachineConstructor extends BParserBaseVisitor<Void> {

		private FormulaASTCreator formulaAstCreator = new FormulaASTCreator();

		MachineConstructor(StartContext start) {
			start.accept(this);
		}

		@Override
		public Void visitMachine_header(BParser.Machine_headerContext ctx) {
			machineNode.setName(ctx.IDENTIFIER().getText());
			// System.out.println("Creating AST for machine " + ctx.IDENTIFIER().getText());
			return null;
		}

		// TODO: Add definitions to Machine AST
		// @Override public Void visitDefinitionClause(BParser.DefinitionClauseContext
		// ctx) { return null; }
		// @Override public T visitDefinitionFile(BParser.DefinitionFileContext ctx) {
		// return visitChildren(ctx); }
		// see DefinitionFinder in DefinitionsAnalyser.java
		@Override
		public Void visitOrdinaryDefinition(BParser.OrdinaryDefinitionContext ctx) {
			final String name = ctx.name.getText();
			Node body = null;
			FormulaContext formula = ctx.formula();
	        if (formula instanceof FormulaPredicateContext) {
	        	body = ((FormulaPredicateContext)formula).predicate().accept(formulaAstCreator);
	        } else if (formula instanceof FormulaSubstitutionContext) {
	        	body = ((FormulaSubstitutionContext)formula).substitution().accept(formulaAstCreator);
	        } else if (formula instanceof FormulaExpressionContext) {
	        	body = ((FormulaExpressionContext)formula).expression().accept(formulaAstCreator);
	        } else {
	        	throw new RuntimeException();
	        }

			List<DeclarationNode> paramNodes = new ArrayList<>();
			if (ctx.parameters != null) {
				paramNodes = createDeclarationList(ctx.parameters.IDENTIFIER(),
						DeclarationNode.Kind.OP_INPUT_PARAMETER);
			}

			DefinitionNode definitionNode = new DefinitionNode(Util.createSourceCodePosition(ctx), name, paramNodes,
					body);
			machineNode.addDefinition(definitionNode);
			return null;
		}

		@Override
		public Void visitInstanceClause(BParser.InstanceClauseContext ctx) {
			MachineReferenceNode.Kind kind = null;
			switch (ctx.name.getType()) {
			case BParser.INCLUDES:
				kind = MachineReferenceNode.Kind.INCLUDED;
				break;
			case BParser.EXTENDS:
				kind = MachineReferenceNode.Kind.EXTENDED;
				break;
			case BParser.IMPORTS:
				kind = MachineReferenceNode.Kind.IMPORTED;
				break;
			default:
				throw new RuntimeException("Unknown instance type: " + ctx.name.getText());
			}
			for (Machine_instantiationContext instance : ctx.machine_instantiation()) {
				String prefix = instance.prefix == null ? null : instance.prefix.getText();
				String machineName = instance.name.getText();
				machineNode.addMachineReferenceNode(
						new MachineReferenceNode(Util.createSourceCodePosition(ctx), machineName, kind, prefix, true));
			}
			return null;
		}

		@Override
		public Void visitReferenceClause(BParser.ReferenceClauseContext ctx) {
			Token referenceKindToken = ctx.name;

			for (BParser.Composed_identifierContext instance : ctx.composed_identifier_list().idents) {
				String prefix = null;
				String name;
				if (instance.IDENTIFIER().size() > 1) {
					prefix = instance.IDENTIFIER().get(0).toString();
					name = instance.IDENTIFIER().get(1).toString();
				} else {
					name = instance.IDENTIFIER().get(0).toString();
				}
				if (referenceKindToken.getType() == BParser.SEES || referenceKindToken.getType() == BParser.USES) {
					MachineReferenceNode.Kind kind = referenceKindToken.getType() == BParser.SEES
							? MachineReferenceNode.Kind.SEEN
							: MachineReferenceNode.Kind.USED;
					machineNode.addMachineReferenceNode(
							new MachineReferenceNode(Util.createSourceCodePosition(ctx), name, kind, prefix, true));
				} else if (referenceKindToken.getType() == BParser.PROMOTES) {
					machineNode.addOperationReferenceNode(
							new OperationReferenceNode(Util.createSourceCodePosition(ctx), name, prefix, true));
				} else {
					throw new RuntimeException("Reference type of ReferenceClauseContext is unknown");
				}
			}
			return null;
		}

		@Override
		public Void visitEnumeratedSet(BParser.EnumeratedSetContext ctx) {
			SourceCodePosition position = getSourcePositionFromTerminalNode(ctx.IDENTIFIER());
			DeclarationNode declarationNode = new DeclarationNode(position, ctx.IDENTIFIER().getSymbol().getText(),
					DeclarationNode.Kind.ENUMERATED_SET, machineNode);
			machineNode.addSetEnumeration(
					new EnumeratedSetDeclarationNode(position, declarationNode, createDeclarationList(
							ctx.identifier_list().IDENTIFIER(), DeclarationNode.Kind.ENUMERATED_SET_ELEMENT)));
			return null;
		}

		@Override
		public Void visitDeferredSet(BParser.DeferredSetContext ctx) {
			SourceCodePosition position = getSourcePositionFromTerminalNode(ctx.IDENTIFIER());
			DeclarationNode declarationNode = new DeclarationNode(position, ctx.IDENTIFIER().getSymbol().getText(),
					DeclarationNode.Kind.DEFERRED_SET, machineNode);
			machineNode.addDeferredSet(declarationNode);
			return null;
		}

		@Override
		public Void visitFreetype_definition(BParser.Freetype_definitionContext ctx) {
			SourceCodePosition position = getSourcePositionFromTerminalNode(ctx.IDENTIFIER());
			DeclarationNode declarationNode = new DeclarationNode(position, ctx.IDENTIFIER().getSymbol().getText(),
					DeclarationNode.Kind.FREETYPE, machineNode);
			List<FreetypeBaseElementNode> elements = new ArrayList<>();
			for (BParser.Freetype_constructorContext constructorCtx : ctx.constructors) {
				if (constructorCtx instanceof BParser.ElementContext) {
					BParser.ElementContext e = (BParser.ElementContext) constructorCtx;
					elements.add(new FreetypeElementNode(
							getSourcePositionFromTerminalNode(e.IDENTIFIER()),
							e.IDENTIFIER().getSymbol().getText(),
							machineNode
					));
				} else if (constructorCtx instanceof BParser.ConstructorContext) {
					BParser.ConstructorContext c = (BParser.ConstructorContext) constructorCtx;
					ExprNode expr = (ExprNode) c.expr.accept(formulaAstCreator);
					elements.add(new FreetypeConstructorNode(
							getSourcePositionFromTerminalNode(c.IDENTIFIER()),
							c.IDENTIFIER().getSymbol().getText(),
							machineNode,
							expr
					));
				} else {
					unreachable();
				}
			}
			machineNode.addFreetype(new FreetypeDeclarationNode(position, declarationNode, elements));
			return null;
		}

		@Override
		public Void visitDeclarationClause(DeclarationClauseContext ctx) {
			LinkedHashMap<String, TerminalNode> declarations = new LinkedHashMap<>();
			for (TerminalNode terminalNode : ctx.identifier_list().IDENTIFIER()) {
				declarations.put(terminalNode.getSymbol().getText(), terminalNode);
			}
			switch (ctx.name.getText()) {
			case "CONSTANTS":
			case "ABSTRACT_CONSTANTS":
			case "CONCRETE_CONSTANTS":
				machineNode.addConstants(
						createDeclarationList(ctx.identifier_list().IDENTIFIER(), DeclarationNode.Kind.CONSTANT));
				break;
			case "VARIABLES":
			case "ABSTRACT_VARIABLES":
			case "CONCRETE_VARIABLES":
				machineNode.addVariables(
						createDeclarationList(ctx.identifier_list().IDENTIFIER(), DeclarationNode.Kind.VARIABLE));
				break;
			default:
				unreachable();
			}
			return null;
		}

		@Override
		public Void visitInitialisationClause(BParser.InitialisationClauseContext ctx) {
			SubstitutionNode subNode = (SubstitutionNode) ctx.substitution().accept(formulaAstCreator);
			machineNode.setInitialisation(subNode);
			return null;
		}

		@Override
		public Void visitValuesClause(BParser.ValuesClauseContext ctx) {
			for (int i = 0; i < ctx.idents.size(); i++) {
				String name = ctx.idents.get(i).getText();
				IdentifierExprNode identifier = new IdentifierExprNode(Util.createSourceCodePosition(ctx.idents.get(i)),
						name, false);
				ExprNode expr = (ExprNode) ctx.exprs.get(i).accept(formulaAstCreator);
				machineNode.addValues(new AssignSubstitutionNode(Util.createSourceCodePosition(ctx),
						Collections.singletonList(identifier), Collections.singletonList(expr)));
			}
			return null;
		}

		@Override
		public Void visitAssertionClause(BParser.AssertionClauseContext ctx) {
			List<PredicateNode> preds = ctx.predicate().stream()
					.map(pred -> (PredicateNode) pred.accept(formulaAstCreator)).collect(Collectors.toList());
			machineNode.setAssertions(preds);
			return null;
		}

		@Override
		public Void visitPredicateClause(BParser.PredicateClauseContext ctx) {
			PredicateNode pred = (PredicateNode) ctx.pred.accept(formulaAstCreator);
			switch (ctx.name.getText()) {
			case "INVARIANT":
				machineNode.setInvariant(pred);
				break;
			case "PROPERTIES":
				machineNode.setProperties(pred);
				break;
			default:
				unreachable();
			}
			return null;
		}

		@Override
		public Void visitBOperation(BParser.BOperationContext ctx) {
			List<DeclarationNode> outputParamNodes = new ArrayList<>();
			if (ctx.output != null) {
				outputParamNodes = createDeclarationList(ctx.output.IDENTIFIER(),
						DeclarationNode.Kind.OP_OUTPUT_PARAMETER);
			}
			List<DeclarationNode> paramNodes = new ArrayList<>();
			if (ctx.parameters != null) {
				paramNodes = createDeclarationList(ctx.parameters.IDENTIFIER(),
						DeclarationNode.Kind.OP_INPUT_PARAMETER);
			}
			SubstitutionNode sub = (SubstitutionNode) ctx.substitution().accept(formulaAstCreator);
			String opName = ctx.IDENTIFIER().getText();
			OperationNode operationNode = new OperationNode(Util.createSourceCodePosition(ctx), opName,
					outputParamNodes, sub, paramNodes);
			machineNode.addOperation(operationNode);
			return null;
		}

		private void unreachable() {
			throw new RuntimeException();
		}

		private List<DeclarationNode> createDeclarationList(List<TerminalNode> list, DeclarationNode.Kind kind) {
			List<DeclarationNode> declarationList = new ArrayList<>();
			for (TerminalNode terminalNode : list) {
				DeclarationNode declNode = new DeclarationNode(getSourcePositionFromTerminalNode(terminalNode),
						terminalNode.getSymbol().getText(), kind, machineNode);
				declarationList.add(declNode);
			}
			return declarationList;
		}

		private SourceCodePosition getSourcePositionFromTerminalNode(TerminalNode terminalNode) {
			return new SourceCodePosition(
				terminalNode.getSymbol().getLine(),
				terminalNode.getSymbol().getCharPositionInLine(),
				terminalNode.getSymbol().getText()
			);
		}

	}
}