package de.prob.parser.antlr;

import de.prob.parser.ast.nodes.DeclarationNode;
import de.prob.parser.ast.nodes.MachineNode;
import de.prob.parser.ast.nodes.OperationNode;
import de.prob.parser.ast.nodes.expression.ExprNode;
import de.prob.parser.ast.nodes.expression.ExpressionOperatorNode;
import de.prob.parser.ast.nodes.expression.IdentifierExprNode;
import de.prob.parser.ast.nodes.expression.IfExpressionNode;
import de.prob.parser.ast.nodes.expression.LambdaNode;
import de.prob.parser.ast.nodes.expression.LetExpressionNode;
import de.prob.parser.ast.nodes.expression.NumberNode;
import de.prob.parser.ast.nodes.expression.QuantifiedExpressionNode;
import de.prob.parser.ast.nodes.expression.RecordFieldAccessNode;
import de.prob.parser.ast.nodes.expression.RecordNode;
import de.prob.parser.ast.nodes.expression.SetComprehensionNode;
import de.prob.parser.ast.nodes.expression.StringNode;
import de.prob.parser.ast.nodes.expression.StructNode;
import de.prob.parser.ast.nodes.ltl.LTLBPredicateNode;
import de.prob.parser.ast.nodes.ltl.LTLInfixOperatorNode;
import de.prob.parser.ast.nodes.ltl.LTLKeywordNode;
import de.prob.parser.ast.nodes.ltl.LTLPrefixOperatorNode;
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
import de.prob.parser.ast.visitors.AbstractVisitor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PrologASTPrinter implements AbstractVisitor<String, Void> {

    public String visitMachineNode(MachineNode node) {
        String machineName = node.getName();
        String variables = visitVariables(node.getVariables());
        String constants = visitConstants(node.getConstants());
        String invariant = visitInvariant(node.getInvariant());
        String properties = visitProperties(node.getProperties());
        String initialisation = visitInitialisation(node.getInitialisation());
        String operations = visitOperations(node.getOperations());
        List<String> body = Arrays.asList(variables, constants, invariant, properties, initialisation, operations);
        return String.format("machine(abstract_machine(none, machine(none), machine_header(none, %s, []), [%s]))", machineName, String.join(", ", body));
    }

    public String visitVariables(List<DeclarationNode> variablesNodes) {
        List<String> variables = variablesNodes.stream().map(this::visitDeclarationNode).collect(Collectors.toList());
        return String.format("variables(none, [%s])", String.join(", ", variables));
    }

    public String visitConstants(List<DeclarationNode> constantsNodes) {
        List<String> constants = constantsNodes.stream().map(this::visitDeclarationNode).collect(Collectors.toList());
        return String.format("constants(none, [%s])", String.join(", ", constants));
    }

    public String visitInvariant(PredicateNode node) {
        String invariant = visitPredicateNode(node, null);
        return String.format("invariant(none, %s)", invariant);
    }

    public String visitProperties(PredicateNode node) {
        String properties = visitPredicateNode(node, null);
        return String.format("properties(none, %s)", properties);
    }

    public String visitDeclarationNode(DeclarationNode node) {
        return String.format("identifier(none, %s)", node.getName());
    }

    public String visitInitialisation(SubstitutionNode node) {
        String substitution = visitSubstitutionNode(node, null);
        return String.format("initialisation(none, %s)", substitution);
    }

    public String visitOperations(List<OperationNode> operationNodes) {
        List<String> operations = operationNodes.stream().map(this::visitOperation).collect(Collectors.toList());
        return String.format("operations(none, [%s])", String.join(", ", operations));
    }

    public String visitOperation(OperationNode operationNode) {
        String opName = operationNode.getName();
        List<String> params = operationNode.getParams().stream().map(this::visitDeclarationNode).collect(Collectors.toList());
        List<String> outputs = operationNode.getOutputParams().stream().map(this::visitDeclarationNode).collect(Collectors.toList());
        String substitution = visitSubstitutionNode(operationNode.getSubstitution(), null);
        return String.format("operation(none, %s, [%s], [%s], %s)", opName, String.join(", ", params), String.join(", ", outputs), substitution);
    }

    @Override
    public String visitExprOperatorNode(ExpressionOperatorNode node, Void expected) {
        ExpressionOperatorNode.ExpressionOperator operator = node.getOperator();
        String functor = "";
        if(node.getArity() == 0) {
            switch (operator) {
                case MININT:
                    return "minint(none)";
                case MAXINT:
                    return "maxint(none)";
                case INTEGER:
                    return "integer(none)";
                case NATURAL:
                    return "natural(none)";
                case NATURAL1:
                    return "natural1(none)";
                case INT:
                    return "int(none)";
                case NAT:
                    return "nat(none)";
                case NAT1:
                    return "nat1(none)";
                case STRING:
                    return "string(none)";
                case FALSE:
                    return "bfalse(none)";
                case TRUE:
                    return "btrue(none)";
                case BOOL:
                    return "bool_set(none)";
                case EMPTY_SET:
                    return "empty_set(none)";
                case EMPTY_SEQUENCE:
                    return "empty_sequence(none)";
            }
        } else {
            switch (operator) {
                case PLUS:
                    functor = "plus";
                    break;
                case UNARY_MINUS:
                    functor = "uminus";
                    break;
                case MOD:
                    functor = "mod";
                    break;
                case DIVIDE:
                    functor = "div";
                    break;
                case PRED:
                    functor = "pred";
                    break;
                case SUCC:
                    functor = "succ";
                    break;
                case POWER_OF:
                    functor = "power_of";
                    break;
                case MULT:
                    // TODO: multiply, and cartesian product
                    functor = "multiply";
                    break;
                case MINUS:
                    functor = "minus";
                    break;
                case INTERVAL:
                    functor = "interval";
                    break;
                case SET_ENUMERATION:
                    functor = "set_enumeration";
                    break;
                case MIN:
                    functor = "min";
                    break;
                case MAX:
                    functor = "max";
                    break;
                case SET_SUBTRACTION:
                    functor = "difference";
                    break;
                case INTERSECTION:
                    functor = "intersection";
                    break;
                case UNION:
                    functor = "union";
                    break;
                case COUPLE:
                    functor = "couple";
                    break;
                case DOMAIN:
                    functor = "domain";
                    break;
                case RANGE:
                    functor = "range";
                    break;
                case ID:
                    functor = "id";
                    break;
                case CLOSURE:
                    functor = "closure";
                    break;
                case CLOSURE1:
                    functor = "closure1";
                    break;
                case ITERATE:
                    functor = "iterate";
                    break;
                case PRJ1:
                    functor = "projection1";
                    break;
                case PRJ2:
                    functor = "projection2";
                    break;
                case FNC:
                    functor = "fnc";
                    break;
                case REL:
                    functor = "rel";
                    break;
                case CONCAT:
                    functor = "concat";
                    break;
                case CONC:
                    functor = "conc";
                    break;
                case DIRECT_PRODUCT:
                    functor = "direct_product";
                    break;
                case PARALLEL_PRODUCT:
                    functor = "parallel_product";
                    break;
                case COMPOSITION:
                    functor = "composition";
                    break;
                case DOMAIN_RESTRICTION:
                    functor = "domain_restriction";
                    break;
                case DOMAIN_SUBTRACTION:
                    functor = "domain_substraction";
                    break;
                case RANGE_RESTRICTION:
                    functor = "range_restriction";
                    break;
                case RANGE_SUBTRACTION:
                    functor = "range_substraction";
                    break;
                case INSERT_FRONT:
                    functor = "insert_front";
                    break;
                case INSERT_TAIL:
                    functor = "insert_tail";
                    break;
                case OVERWRITE_RELATION:
                    functor = "overwrite";
                    break;
                case INVERSE_RELATION:
                    functor = "inverse";
                    break;
                case RESTRICT_FRONT:
                    functor = "restrict_front";
                    break;
                case RESTRICT_TAIL:
                    functor = "restrict_tail";
                    break;
                case GENERALIZED_INTER:
                    functor = "generalized_inter";
                    break;
                case GENERALIZED_UNION:
                    functor = "generalized_union";
                    break;
                case SEQ_ENUMERATION:
                    functor = "seq_enumeration";
                    break;
                case LAST:
                    functor = "last";
                    break;
                case FIRST:
                    functor = "first";
                    break;
                case REV:
                    functor = "rev";
                    break;
                case FRONT:
                    functor = "front";
                    break;
                case TAIL:
                    functor = "tail";
                    break;
                case PERM:
                    functor = "perm";
                    break;
                case SEQ:
                    functor = "seq";
                    break;
                case SEQ1:
                    functor = "seq1";
                    break;
                case ISEQ:
                    functor = "iseq";
                    break;
                case ISEQ1:
                    functor = "iseq1";
                    break;
                case FUNCTION_CALL:
                    functor = "function_call";
                    break;
                case RELATIONAL_IMAGE:
                    functor = "relational_image";
                    break;
                case SIZE:
                    functor = "size";
                    break;
                case CARD:
                    functor = "card";
                    break;
                case TOTAL_FUNCTION:
                    functor = "total_function";
                    break;
                case PARTIAL_FUNCTION:
                    functor = "partial_function";
                    break;
                case TOTAL_INJECTION:
                    functor = "total_injection";
                    break;
                case PARTIAL_INJECTION:
                    functor = "partial_injection";
                    break;
                case TOTAL_BIJECTION:
                    functor = "total_bijection";
                    break;
                case PARTIAL_BIJECTION:
                    functor = "partial_bijection";
                    break;
                case TOTAL_SURJECTION:
                    functor = "total_surjection";
                    break;
                case PARTIAL_SURJECTION:
                    functor = "partial_surjection";
                    break;
                case SURJECTION_RELATION:
                    functor = "surjection_relation";
                    break;
                case TOTAL_RELATION:
                    functor = "total_relation";
                    break;
                case TOTAL_SURJECTION_RELATION:
                    functor = "total_surjection_relation";
                    break;
                case SET_RELATION:
                    functor = "set_relation";
                    break;
                case FIN:
                    functor = "fin";
                    break;
                case FIN1:
                    functor = "fin1";
                    break;
                case POW1:
                    functor = "pow1";
                    break;
                case POW:
                    functor = "pow";
                    break;
                default:
                    throw new RuntimeException("Operator is not supported for ExpressionOperatorNode");
            }
            List<String> expressions = node.getExpressionNodes().stream().map(expr -> visitExprNode(expr, expected)).collect(Collectors.toList());
            return String.format("%s(%s)", functor, String.join(", ", expressions));
        }

        return "";
    }

    @Override
    public String visitIdentifierExprNode(IdentifierExprNode node, Void expected) {
        return String.format("identifier(none, %s)", node.getName());
    }

    @Override
    public String visitCastPredicateExpressionNode(CastPredicateExpressionNode node, Void expected) {
        String predicate = visitPredicateNode(node.getPredicate(), expected);
        return String.format("boolean_cast(none, %s)", predicate);
    }

    @Override
    public String visitNumberNode(NumberNode node, Void expected) {
        return String.format("integer(none, %s)", node.getValue());
    }

    @Override
    public String visitQuantifiedExpressionNode(QuantifiedExpressionNode node, Void expected) {
        QuantifiedExpressionNode.QuantifiedExpressionOperator operator = node.getOperator();
        String functor = "";
        switch (operator) {
            case QUANTIFIED_INTER:
                functor = "inter";
                break;
            case QUANTIFIED_UNION:
                functor = "union";
                break;
            default:
                throw new RuntimeException("Operator for QuantifiedExpressionNode is not supported");
        }
        List<String> identifiers = node.getDeclarationList().stream().map(DeclarationNode::getName).collect(Collectors.toList());
        String predicate = visitPredicateNode(node.getPredicateNode(), expected);
        return String.format("%s(none, [%s], %s)", functor, String.join(", ", identifiers), predicate);
    }

    @Override
    public String visitSetComprehensionNode(SetComprehensionNode node, Void expected) {
        List<String> identifiers = node.getDeclarationList().stream().map(DeclarationNode::getName).collect(Collectors.toList());
        String predicate = visitPredicateNode(node.getPredicateNode(), expected);
        return String.format("set_comprehension(none, [%s], %s)", String.join(", ", identifiers), predicate);
    }

    @Override
    public String visitLambdaNode(LambdaNode node, Void expected) {
        List<String> identifiers = node.getDeclarations().stream().map(DeclarationNode::getName).collect(Collectors.toList());
        String predicate = visitPredicateNode(node.getPredicate(), expected);
        String expression = visitExprNode(node.getExpression(), expected);
        return String.format("lambda(none, [%s], %s, %s)", String.join(", ", identifiers), predicate, expression);
    }

    @Override
    public String visitLetExpressionNode(LetExpressionNode node, Void expected) {
        List<String> identifiers = node.getLocalIdentifiers().stream().map(DeclarationNode::getName).collect(Collectors.toList());
        String predicate = visitPredicateNode(node.getPredicate(), expected);
        String body = visitExprNode(node.getExpression(), expected);
        return String.format("let_expr(none, [%s], %s, %s)", String.join(", ", identifiers), predicate, body);
    }

    @Override
    public String visitIfExpressionNode(IfExpressionNode node, Void expected) {
        String predicate = visitPredicateNode(node.getCondition(), expected);
        String thenExpr = visitExprNode(node.getThenExpression(), expected);
        String elseExpr = visitExprNode(node.getElseExpression(), expected);
        return String.format("if_expr(none, %s, %s, %s)", predicate, thenExpr, elseExpr);
    }

    @Override
    public String visitStringNode(StringNode node, Void expected) {
        return String.format("string(none, %s)", node.getValue());
    }

    @Override
    public String visitRecordNode(RecordNode node, Void expected) {
        return null;
    }

    @Override
    public String visitStructNode(StructNode node, Void expected) {
        return null;
    }

    @Override
    public String visitRecordFieldAccessNode(RecordFieldAccessNode node, Void expected) {
        return null;
    }

    @Override
    public String visitLTLPrefixOperatorNode(LTLPrefixOperatorNode node, Void expected) {
        return null;
    }

    @Override
    public String visitLTLKeywordNode(LTLKeywordNode node, Void expected) {
        return null;
    }

    @Override
    public String visitLTLInfixOperatorNode(LTLInfixOperatorNode node, Void expected) {
        return null;
    }

    @Override
    public String visitLTLBPredicateNode(LTLBPredicateNode node, Void expected) {
        return null;
    }

    @Override
    public String visitIdentifierPredicateNode(IdentifierPredicateNode node, Void expected) {
        return String.format("identifier(none, %s)", node.getName());
    }

    @Override
    public String visitPredicateOperatorNode(PredicateOperatorNode node, Void expected) {
        PredicateOperatorNode.PredicateOperator operator = node.getOperator();
        if(node.getPredicateArguments().size() == 0) {
            switch (operator) {
                case TRUE:
                    return "boolean_true(none)";
                case FALSE:
                    return "boolean_false(none)";
                default:
                    throw new RuntimeException("PredicateOperator for PredicateOperatorNode is not supprted");
            }
        } else {
            String functor = "";
            switch (operator) {
                case OR:
                    functor = "or";
                    break;
                case AND:
                    functor = "and";
                    break;
                case NOT:
                    functor = "not";
                    break;
                case IMPLIES:
                    functor = "implies";
                    break;
                case EQUIVALENCE:
                    functor = "equivalence";
                    break;
            }
            List<String> predicates = node.getPredicateArguments().stream().map(pred -> visitPredicateNode(pred, expected)).collect(Collectors.toList());
            return String.format("%s(%s)", functor, String.join(", ", predicates));
        }
    }

    @Override
    public String visitPredicateOperatorWithExprArgs(PredicateOperatorWithExprArgsNode node, Void expected) {
        PredicateOperatorWithExprArgsNode.PredOperatorExprArgs operator = node.getOperator();
        String functor = "";
        switch (operator) {
            case LESS:
                functor = "less";
                break;
            case EQUAL:
                functor = "equal";
                break;
            case GREATER:
                functor = "greater";
                break;
            case INCLUSION:
                functor = "subset_of";
                break;
            case NOT_EQUAL:
                functor = "not_equal";
                break;
            case ELEMENT_OF:
                functor = "element_of";
                break;
            case LESS_EQUAL:
                functor = "less_equal";
                break;
            case GREATER_EQUAL:
                functor = "greater_equal";
                break;
            case NON_INCLUSION:
                functor = "not_subset_of";
                break;
            case NOT_BELONGING:
                functor = "not_belonging";
                break;
            case STRICT_INCLUSION:
                functor = "strict_subset_of";
                break;
            case STRICT_NON_INCLUSION:
                functor = "not_strict_subset_of";
                break;
        }
        List<String> expressions = node.getExpressionNodes().stream().map(expression -> visitExprNode(expression, expected)).collect(Collectors.toList());
        return String.format("%s(%s)", functor, String.join(", ", expressions));
    }

    @Override
    public String visitQuantifiedPredicateNode(QuantifiedPredicateNode node, Void expected) {
        QuantifiedPredicateNode.QuantifiedPredicateOperator operator = node.getOperator();
        String functor = "";
        switch (operator) {
            case UNIVERSAL_QUANTIFICATION:
                functor = "forall";
                break;
            case EXISTENTIAL_QUANTIFICATION:
                functor = "exists";
                break;
            default:
                throw new RuntimeException("Operator for QuantifiedPredicateNode is not supported");
        }
        List<String> identifiers = node.getDeclarationList().stream().map(DeclarationNode::getName).collect(Collectors.toList());
        String predicate = visitPredicateNode(node.getPredicateNode(), expected);
        return String.format("%s(none, [%s], %s)", functor, String.join(", ", identifiers), predicate);
    }

    @Override
    public String visitLetPredicateNode(LetPredicateNode node, Void expected) {
        List<String> identifiers = node.getLocalIdentifiers().stream().map(DeclarationNode::getName).collect(Collectors.toList());
        String predicate = visitPredicateNode(node.getWherePredicate(), expected);
        String body = visitPredicateNode(node.getPredicate(), expected);
        return String.format("let_pred(none, [%s], %s, %s)", String.join(", ", identifiers), predicate, body);
    }

    @Override
    public String visitIfPredicateNode(IfPredicateNode node, Void expected) {
        String predicate = visitPredicateNode(node.getCondition(), expected);
        String thenPred = visitPredicateNode(node.getThenPredicate(), expected);
        String elsePred = visitPredicateNode(node.getElsePredicate(), expected);
        return String.format("if_pred(none, %s, %s, %s)", predicate, thenPred, elsePred);
    }

    @Override
    public String visitVarSubstitutionNode(VarSubstitutionNode node, Void expected) {
        List<String> identifiers = node.getLocalIdentifiers().stream().map(DeclarationNode::getName).collect(Collectors.toList());
        String body = visitSubstitutionNode(node.getBody(), expected);
        return String.format("var(none, [%s], %s)", String.join(", ", identifiers), body);
    }

    @Override
    public String visitWhileSubstitutionNode(WhileSubstitutionNode node, Void expected) {
        String condition = visitPredicateNode(node.getCondition(), expected);
        String body = visitSubstitutionNode(node.getBody(), expected);
        String variant = visitExprNode(node.getVariant(), expected);
        String invariant = visitPredicateNode(node.getInvariant(), expected);
        return String.format("while(none, %s, %s, %s, %s)", condition, body, variant, invariant);
    }

    @Override
    public String visitListSubstitutionNode(ListSubstitutionNode node, Void expected) {
        ListSubstitutionNode.ListOperator operator = node.getOperator();
        List<String> substitutions = node.getSubstitutions().stream().map(substitution -> visitSubstitutionNode(substitution, expected)).collect(Collectors.toList());
        switch (operator) {
            case Parallel:
                return String.format("parallel(none, [%s])", String.join(", ", substitutions));
            case Sequential:
                return String.format("sequential(none, [%s])", String.join(", ", substitutions));
            default:
                throw new RuntimeException("List operator for ListSubstitutionNode is not supported");
        }
    }

    @Override
    public String visitIfOrSelectSubstitutionsNode(IfOrSelectSubstitutionsNode node, Void expected) {
        IfOrSelectSubstitutionsNode.Operator operator = node.getOperator();
        switch (operator) {
            case SELECT:
                PredicateNode predicateNode = node.getConditions().get(0);
                SubstitutionNode substitutionNode = node.getSubstitutions().get(0);
                String predicate = visitPredicateNode(predicateNode, expected);
                String substitution = visitSubstitutionNode(substitutionNode, expected);
                return String.format("select(none, %s, %s)", predicate, substitution);
            case IF:
                // TODO
                break;
        }
        return null;
    }

    @Override
    public String visitAssignSubstitutionNode(AssignSubstitutionNode node, Void expected) {
        List<String> lhsAST = node.getLeftSide().stream().map(lhs -> visitExprNode(lhs, expected)).collect(Collectors.toList());
        List<String> rhsAST = node.getRightSide().stream().map(rhs -> visitExprNode(rhs, expected)).collect(Collectors.toList());
        return String.format("assign(none, [%s], [%s])", String.join(", ", lhsAST), String.join(", ", rhsAST));
    }

    @Override
    public String visitSkipSubstitutionNode(SkipSubstitutionNode node, Void expected) {
        return "skip(none)";
    }

    @Override
    public String visitConditionSubstitutionNode(ConditionSubstitutionNode node, Void expected) {
        ConditionSubstitutionNode.Kind kind = node.getKind();
        String substitution = visitSubstitutionNode(node.getSubstitution(), expected);
        switch (kind) {
            case ASSERT:
                return String.format("assert(none, %s)", substitution);
            case PRECONDITION:
                return String.format("precondition(none, %s)", substitution);
            default:
                throw new RuntimeException("Kind for ConditionSubstitutionNode is not supported");
        }
    }

    @Override
    public String visitAnySubstitution(AnySubstitutionNode node, Void expected) {
        List<String> identifiers = node.getParameters().stream().map(DeclarationNode::getName).collect(Collectors.toList());
        String predicate = visitPredicateNode(node.getWherePredicate(), expected);
        String body = visitSubstitutionNode(node.getThenSubstitution(), expected);
        return String.format("any(none, [%s], %s, %s)", String.join(", ", identifiers), predicate, body);
    }

    @Override
    public String visitLetSubstitution(LetSubstitutionNode node, Void expected) {
        List<String> identifiers = node.getLocalIdentifiers().stream().map(DeclarationNode::getName).collect(Collectors.toList());
        String predicate = visitPredicateNode(node.getPredicate(), expected);
        String body = visitSubstitutionNode(node.getBody(), expected);
        return String.format("let(none, [%s], %s, %s)", String.join(", ", identifiers), predicate, body);
    }

    @Override
    public String visitBecomesElementOfSubstitutionNode(BecomesElementOfSubstitutionNode node, Void expected) {
        List<String> lhsAST = node.getIdentifiers().stream().map(lhs -> visitExprNode(lhs, expected)).collect(Collectors.toList());
        String rhsAST = visitExprNode(node.getExpression(), expected);
        return String.format("becomes_element_of(none, [%s], %s)", String.join(", ", lhsAST), rhsAST);
    }

    @Override
    public String visitBecomesSuchThatSubstitutionNode(BecomesSuchThatSubstitutionNode node, Void expected) {
        List<String> lhsAST = node.getIdentifiers().stream().map(lhs -> visitExprNode(lhs, expected)).collect(Collectors.toList());
        String rhsAST = visitPredicateNode(node.getPredicate(), expected);
        return String.format("becomes_such_that(none, [%s], %s)", String.join(", ", lhsAST), rhsAST);
    }

    @Override
    public String visitSubstitutionIdentifierCallNode(OperationCallSubstitutionNode node, Void expected) {
        List<String> assignedVariables = node.getAssignedVariables().stream().map(var -> visitExprNode(var, expected)).collect(Collectors.toList());
        String opName = node.getOperationNode().getName();
        List<String> arguments = node.getArguments().stream().map(arg -> visitExprNode(arg, expected)).collect(Collectors.toList());
        return String.format("op_call(none, [%s], %s, [%s])", String.join(", ", assignedVariables), opName, String.join(", ", arguments));
    }

    @Override
    public String visitChoiceSubstitutionNode(ChoiceSubstitutionNode node, Void expected) {
        List<String> substitutions = node.getSubstitutions().stream().map(substitution -> visitSubstitutionNode(substitution, expected)).collect(Collectors.toList());
        return String.format("choice(none, [%s])", String.join(", ", substitutions));
    }
}
