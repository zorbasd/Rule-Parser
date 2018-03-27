package com.rules;

import com.rules.engine.RuleParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple RuleParser.
 */
public class RuleParserTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RuleParserTest(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(RuleParserTest.class);
    }

    /**
     * The purpose of this method is to test the isValidSyntax method
     */
    public void testValidRule()
    {
        //-----------------------------------------------------------------
        //--------------------POSITIVE TEST CASES--------------------------
        //-----------------------------------------------------------------
        RuleParser ruleParser = new RuleParser("Bx1");
        assertTrue(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("Bx1 Or Ax2");
        assertTrue(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("Bx1 xOr Ax2");
        assertTrue(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("(Cx1 xOr Cx2) AND ((Dx1 AND Dx4) OR Dx1)");
        assertTrue(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("SKUx1 Or SKUx2");
        assertTrue(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("(SKUx1)");
        assertTrue(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("((SKUx1) aNd (((SKUx2 oR (SKUx3)))))");
        assertTrue(ruleParser.isValidSyntax());

        //-----------------------------------------------------------------
        //--------------------NEGATIVE TEST CASES--------------------------
        //-----------------------------------------------------------------
        ruleParser = new RuleParser("");
        assertFalse(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("()");
        assertFalse(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("SKU)");
        assertFalse(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("(SKU");
        assertFalse(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("(SKU)(SKU)");
        assertFalse(ruleParser.isValidSyntax());
        
        ruleParser = new RuleParser("SKU SKU");
        assertFalse(ruleParser.isValidSyntax());
        
        ruleParser = new RuleParser("SKU AND SKU");
        assertFalse(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("OR");
        assertFalse(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("SKU1 OR");
        assertFalse(ruleParser.isValidSyntax());

        ruleParser = new RuleParser("SKUx1 XOR SKU1");
        assertFalse(ruleParser.isValidSyntax());
    }
}
