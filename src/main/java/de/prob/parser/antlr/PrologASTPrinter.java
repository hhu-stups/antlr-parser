package de.prob.parser.antlr;

import de.prob.parser.ast.nodes.DeclarationNode;
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
import de.prob.parser.ast.nodes.substitution.VarSubstitutionNode;
import de.prob.parser.ast.nodes.substitution.WhileSubstitutionNode;
import de.prob.parser.ast.visitors.AbstractVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class PrologASTPrinter implements AbstractVisitor<String, Void> {


    @Override
    public String visitExprOperatorNode(ExpressionOperatorNode node, Void expected) {
        List<ExprNode> expressionNodes = node.getExpressionNodes();
        ExpressionOperatorNode.ExpressionOperator operator = node.getOperator();
        String functor = "";
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
            case MINUS:
                functor = "minus";
                break;
            case INTERVAL:
                functor = "interval";
                break;
            case SET_ENUMERATION:
            case MIN:
                functor = "min";
                break;
            case MAX:
                functor = "max";
                break;
            case MININT:
            case MAXINT:
            case INTEGER:
            case NATURAL:
            case NATURAL1:
            case INT:
            case NAT:
            case NAT1:
            case STRING:
            case FALSE:
            case TRUE:
            case BOOL:
            case SET_SUBTRACTION:
            case INTERSECTION:
            case UNION:
            case COUPLE:
            case DOMAIN:
            case RANGE:
            case ID:
            case CLOSURE:
            case CLOSURE1:
            case ITERATE:
            case PRJ1:
            case PRJ2:
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
            case COMPOSITION:
            case DOMAIN_RESTRICTION:
            case DOMAIN_SUBTRACTION:
            case RANGE_RESTRICTION:
            case RANGE_SUBTRACTION:
            case INSERT_FRONT:
            case INSERT_TAIL:
            case OVERWRITE_RELATION:
            case INVERSE_RELATION:
            case RESTRICT_FRONT:
            case RESTRICT_TAIL:
            case GENERALIZED_INTER:
            case GENERALIZED_UNION:
            case EMPTY_SEQUENCE:
            case SEQ_ENUMERATION:
            case LAST:
            case FIRST:
            case REV:
            case FRONT:
            case TAIL:
            case PERM:
            case SEQ:
            case SEQ1:
            case ISEQ:
            case ISEQ1:
            case FUNCTION_CALL:
            case RELATIONAL_IMAGE:
            case SIZE:
            case CARD:
            case EMPTY_SET:
            case TOTAL_FUNCTION:
            case PARTIAL_FUNCTION:
            case TOTAL_INJECTION:
            case PARTIAL_INJECTION:
            case TOTAL_BIJECTION:
            case PARTIAL_BIJECTION:
            case TOTAL_SURJECTION:
            case PARTIAL_SURJECTION:
            case SURJECTION_RELATION:
            case TOTAL_RELATION:
            case TOTAL_SURJECTION_RELATION:
            case SET_RELATION:
            case FIN:
            case FIN1:
            case POW1:
            case POW:
            default:
                throw new AssertionError();
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
        return null;
    }

    @Override
    public String visitChoiceSubstitutionNode(ChoiceSubstitutionNode node, Void expected) {
        List<String> substitutions = node.getSubstitutions().stream().map(substitution -> visitSubstitutionNode(substitution, expected)).collect(Collectors.toList());
        return String.format("choice(none, [%s])", String.join(", ", substitutions));
    }
}
