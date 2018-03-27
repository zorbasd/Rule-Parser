package com.rules.engine;

import com.udojava.evalex.AbstractOperator;
import com.udojava.evalex.Expression;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The purpose of this class is to parse the combination Rules.
 * 
 * @author Ammar Mohammed
 */
public class RuleParser {
    private static Logger log = LoggerFactory.getLogger(RuleParser.class);

    // Class Constants
    private final String OPEN_BRACKET  = "(";
    private final String CLOSE_BRACKET = ")";
    private final String AND           = "AND";
    private final String XOR           = "XOR";
    private final String OR            = "OR";

    // Class variables
    private List<String>  tokens;
    private String        currentToken;
    private Integer       bracketCounter = 0;
    private Iterator      tokensIterator = null;
    private MachineStates currentState   = MachineStates.FIRST;

    private HashMap<String, Integer> expectedProductCount = new HashMap<String, Integer>();
    private HashMap<String, Integer> actualProductCount   = new HashMap<String, Integer>();

    /**
     * The purpose of this method is to validate a rule condition.
     * 
     * @param condition String The rule condition to be validated
     */
    public RuleParser(String Rule) {
        log.debug("Rule Parser: Working with rule ({})...", Rule);

        // Converting the raw Rule to token list
        this.tokens = new LinkedList(Arrays.asList(
            Rule.toUpperCase().trim().replace("(", " ( ").replace(")", " ) ").split(" ")
        ));

        // Now, cleaning the Rule token list
        tokens.removeAll(Arrays.asList("", null));
    }

    /**
     * This method implements a finite state machine that parses rules. The state machine have two states, namely:<p>
     * 
     * 1) FIRST:  which accepts either OPEN_BRACKET or a condition. A condition will change the state to SECOND.<P>
     * 2) SECOND: which accepts either a CLOSE_BRACKET (only if bracketCounter positive) or and AND/OR/XOR. AND/OR/XOR will change the state to FIRST.<P>
     */
    private boolean runFiniteStateMachine() {
        if (tokensIterator.hasNext()) {
            currentToken = (String) tokensIterator.next();
        } else {
            return (bracketCounter == 0) && (currentState == MachineStates.SECOND);
        }

        switch (currentState) {
            case FIRST:
                // Invalid input: expecting and opening bracket or an item
                if (!currentToken.equals(OPEN_BRACKET) && !isValidCondition(currentToken)) {
                    log.debug("Rule Parser: Unexpected OR, XOR, AND, or a closing bracket encountered.");
                    return false;
                }

                // Valid input: update the machine's state or parameters
                if (currentToken.equals(OPEN_BRACKET)) {
                    bracketCounter++;
                } else {
                    currentState = MachineStates.SECOND;
                }

                break;
            case SECOND:
                // Invalid input: expecting an "and", and "OR" or a closeing bracket
                if (!currentToken.equals(AND) && !currentToken.equals(OR) && !currentToken.equals(XOR) && !currentToken.equals(CLOSE_BRACKET)) {
                    log.debug("Rule Parser: Unexpected opening bracket or a condition encountered.");
                    return false;
                }

                // Valid input: update the machine's state or parameters
                if (currentToken.equals(AND) || currentToken.equals(OR) || currentToken.equals(XOR)) {
                    currentState = MachineStates.FIRST;
                } else if (currentToken.equals(CLOSE_BRACKET) && --bracketCounter < 0) {
                    log.debug("Rule Parser: Invalid closing bracket encountered.");
                    return false;
                }

                break;
            default:
                return false;
        }

        return runFiniteStateMachine();
    }

    /**
     * The purpose of this method is to validate a rule condition. A rule conditon must follow the {{item}}X{{item_cound}}
     * Where {{item}} is the numerical item Id and {{item_count}} is the number of times the item is expected to be observed
     * 
     * @param condition String The rule condition to be validated
     */
    private boolean isValidCondition(String condition) {
        // A rule condition cannot be a closing bracket, and and or and or
        if (condition == CLOSE_BRACKET || condition == AND || condition == OR || condition == XOR) {
            return false;
        }

        // A rule condition must have exactly to elements, the item and the item count
        String[] conditionDetails = condition.split("X");
        if (conditionDetails.length != 2) {
            return false;
        }

        String  item;
        Integer itemCount;

        try {
            item    = conditionDetails[0];
            itemCount = Integer.parseInt(conditionDetails[1]);
        } catch (Exception e) {
            log.error("Rule Parser: Invalid value for an item cound {}.", conditionDetails[1]);
            return false;
        }

        expectedProductCount.put(item, itemCount);
        actualProductCount.put(item,   0);

        return true;
    }

    /**
     * The purpose of this method is to validate the syntax of the rule under inspection.
     */
    public boolean isValidSyntax() {
        bracketCounter = 0;
        currentState   = MachineStates.FIRST;
        tokensIterator = tokens.iterator();

        return tokensIterator.hasNext() && runFiniteStateMachine();
    }

    /**
     * The purpose of this method is to whether a list of item Ids matchs the rule.
     * 
     * @param item List<String> The list of item Ids to be validated
     */
    public boolean isMatchingRule(List<String> items) {
        // First, parse and validate the syntax of the rule
        if (!isValidSyntax()) {
            return false;
        }

        // Getting the actual item count
        for (String item : items) {
            item = item.toUpperCase();
            if (!actualProductCount.containsKey(item)) {
                return false;
            }

            actualProductCount.put(item, actualProductCount.get(item)+1);
        }

        // Building the boolean expression for the rule
        String  expression    = String.join(" ", this.tokens).replace("XOR", "@").replace("AND", "&&").replace("OR", "||");
        String  condition     = "";
        Integer extraProducts = 0;
        for (String item : expectedProductCount.keySet()) {
            condition = item + "X" + expectedProductCount.get(item);

            if (expectedProductCount.get(item) == actualProductCount.get(item)) {
                expression = expression.replace(condition, "TRUE");
            } else {
                expression    = expression.replace(condition, "FALSE");
                // If the condition is not met, and expected count is greater than zeor, this means we have extra items provided
                extraProducts += actualProductCount.get(item) != 0 ? 1 : 0;
            }
        }

        try {
            Expression expressionObject = new Expression(expression);

            // Mimiking the execlusive or operation
            expressionObject.addOperator(new AbstractOperator("@", 3, false) {
                @Override
                public BigDecimal eval(BigDecimal operand1, BigDecimal operand2) {
                    return (operand1.equals(BigDecimal.ONE) ^ operand2.equals(BigDecimal.ONE)) ? BigDecimal.ONE : BigDecimal.ZERO;
                }
            });

            return ((extraProducts == 0) && (expressionObject.eval().intValue() == 1)) ? true : false;
        } catch (Exception e) {
            log.error("Rule Parser: Encounterd an exception while evaluating the rule expression {}.", e);
            return false;
        }
    }

    public void testRule(List<String> items) {
        Boolean isValid = this.isValidSyntax();
        System.out.println("Rule: " + String.join(" ", this.tokens) + ", Is Rule Valid? " + (isValid ? "Yes" : "No") + ", Input: " + items + ", Are Items Matching? " + this.isMatchingRule(items));
    }
}
