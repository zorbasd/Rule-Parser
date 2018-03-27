package com.rules;

import java.util.Arrays;
import com.rules.engine.RuleParser;

public class Example {
    public static void main(String[] args) {
        System.out.println("--------------------------------------------------------");
        System.out.println("--------------------RULE PARSER EXAMPLE-----------------");
        System.out.println("--------------------------------------------------------");

        String rule1 = "(Ax2 XOR Cx1 XOR Bx3) AND ((Dx1 AND Ex3) OR Dx1)";
        String rule2 = "(Ax2)";
        String rule3 = "Yx1 OR Zx3 OR 1X3";
        String rule4 = "Yx1 XOR Zx3 XOR 1X3";

        RuleParser parser = new RuleParser(rule1);
        System.out.println("\nSuccesses\n");
        parser.testRule(Arrays.asList(new String[]{"A", "A", "D"}));
        parser.testRule(Arrays.asList(new String[]{"D", "E", "E", "E", "C"}));
        parser.testRule(Arrays.asList(new String[]{"B", "B", "B", "D"}));
        parser.testRule(Arrays.asList(new String[]{"C", "D"}));

        System.out.println("\nFailures\n");
        parser.testRule(Arrays.asList(new String[]{"W", "A", "D"}));
        parser.testRule(Arrays.asList(new String[]{"D", "E", "E", "C"}));
        parser.testRule(Arrays.asList(new String[]{"B", "B", "D"}));
        parser.testRule(Arrays.asList(new String[]{"C", "D", "A"}));
        parser.testRule(Arrays.asList(new String[]{"A", "A", "D", "C"}));

        parser = new RuleParser(rule2);
        System.out.println("\nSuccesses\n");
        parser.testRule(Arrays.asList(new String[]{"A", "A"}));

        System.out.println("\nFailures\n");
        parser.testRule(Arrays.asList(new String[]{"W", "A", "D"}));
        parser.testRule(Arrays.asList(new String[]{"A", "A", "A"}));
        parser.testRule(Arrays.asList(new String[]{"B", "B", "D"}));
        parser.testRule(Arrays.asList(new String[]{"A"}));

        parser = new RuleParser(rule3);
        System.out.println("\nSuccesses\n");
        parser.testRule(Arrays.asList(new String[]{"1", "1", "1"}));
        parser.testRule(Arrays.asList(new String[]{"z", "z", "z", "Y"}));
        parser.testRule(Arrays.asList(new String[]{"Y"}));

        System.out.println("\nFailures\n");
        parser.testRule(Arrays.asList(new String[]{"W", "A", "D"}));
        parser.testRule(Arrays.asList(new String[]{"D", "E", "E", "C"}));
        parser.testRule(Arrays.asList(new String[]{"Y", "1", "Z"}));
        parser.testRule(Arrays.asList(new String[]{"C", "D", "A"}));
        parser.testRule(Arrays.asList(new String[]{"A", "A", "D", "C"}));

        parser = new RuleParser(rule4);
        System.out.println("\nSuccesses\n");
        parser.testRule(Arrays.asList(new String[]{"1", "1", "1"}));
        parser.testRule(Arrays.asList(new String[]{"z", "z", "z"}));
        parser.testRule(Arrays.asList(new String[]{"Y"}));

        System.out.println("\nFailures\n");
        parser.testRule(Arrays.asList(new String[]{"z", "z", "z", "1", "1", "1", "Y"}));
        parser.testRule(Arrays.asList(new String[]{"z", "z", "z", "Y"}));
        parser.testRule(Arrays.asList(new String[]{"1", "1", "1", "Y"}));
        parser.testRule(Arrays.asList(new String[]{"z", "z", "z", "1", "1", "1"}));

        String rule   = "Carx2 And Bikex1";
         parser = new RuleParser(rule);

        parser.testRule(Arrays.asList(new String[]{"Car", "Car", "Bike"}));
        parser.testRule(Arrays.asList(new String[]{"Car", "Car", "Bike", "Car"}));
    }
}