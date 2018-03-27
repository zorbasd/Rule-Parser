# Rule Parser
Rule Parser is a simple fun tool for parsing rules of the form `(AxN XOR CxM OR BxW) AND ((DxW AND ExT) OR DxM)`.

## Instructions
Given the following input:

- A rule of the form `(AxN XOR CxM OR BxW) AND ((DxW AND ExT) OR DxM)` where `A, B, C, D, and E` are items, and `N, M, W, T, M` are frequencies (i.e. the number of time the items are expected to be observed);
- A list of items like `[A, A, B, C, D, A, A]`

The Rule parser will first parse the `rule` (first input), and then it validates whether the list of `items` (second input) matchs the `rule`.
For example, if the `rule` says `"Carx2 And Bikex1"` and the `items` are `["Car", "Car", "Bike", "Car"]` the parser will fail.

```java
		String     rule   = "Carx2 And Bikex1";
		RuleParser parser = new RuleParser(rule);

		parser.testRule(Arrays.asList(new String[]{"Car", "Car", "Bike"}));
		/* 
			Is Valid, and prints:
			Rule: CARX2 AND BIKEX1, Is Rule Valid? Yes, Input: [Car, Car, Bike], Are Items Matching? true
		 */
		parser.testRule(Arrays.asList(new String[]{"Car", "Car", "Bike", "Car"}));
		/* 
			Is Invalid, and prints:
			Rule: CARX2 AND BIKEX1, Is Rule Valid? Yes, Input: [Car, Car, Bike, Car], Are Items Matching? false
		 */
```