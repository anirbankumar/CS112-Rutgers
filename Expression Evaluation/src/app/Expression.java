package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/(/)\\[\\]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is
	 * created and stored, even if it appears more than once in the expression. At
	 * this time, values for all variables and all array items are set to zero -
	 * they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr   The expression
	 * @param vars   The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		/**
		 * DO NOT create new vars and arrays - they are already created before being
		 * sent in to this method - you just need to fill them in.
		 **/
		
		StringTokenizer tokenizer = new StringTokenizer(expr, delims);
		String[] actualTokens = new String[tokenizer.countTokens()];

		int counter = 0;
		while (tokenizer.hasMoreTokens() == true) {
			actualTokens[counter] = tokenizer.nextToken();
			counter++;
		}
		counter = 0;
		for (int i = 0; i < actualTokens.length; i++) {
			String token = actualTokens[i];
			if (!token.equals("") && !(token.charAt(0) >= '0' && token.charAt(0) <= '9')) {
				counter = expr.indexOf(token, counter) + token.length();
				if (counter < expr.length() && expr.charAt(counter) == '[' && !arrays.contains(new Array(token))) {
					arrays.add(new Array(token));
				} else if (!vars.contains(new Variable(token))) {
					vars.add(new Variable(token));
				}
			} else {
				continue;
			}
		}
	}

	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input
	 * @param vars   The variables array list, previously populated by
	 *               makeVariableLists
	 * @param arrays The arrays array list - previously populated by
	 *               makeVariableLists
	 */
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) {
				vars.get(vari).value = num;
			} else {
				arr = arrays.get(arri);
				arr.values = new int[num];
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars   The variables array list, with values for all variables in the
	 *               expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

		String[] expressionTokens = expr.split("(?<=[" + delims + "])|(?=[" + delims + "])");
		Stack<Float> values = new Stack<Float>();
		Stack<String> operations = new Stack<String>();
		values.clear();
		operations.clear();

		for (int i = 0; i < expressionTokens.length; i++) {
			String token = expressionTokens[i];
			if (token.equals(" ")) {
				continue;
			}
			if (isNumeric(token)) {
				values.push(Float.parseFloat(token));

			} else if (token.equals("(") || token.equals("[")) {
				operations.push(token);
			} else if (token.equals(")")) {
				while (!(operations.peek().equals("("))) {
					if (operations.peek().equals("[")) {
						operations.pop();
					} else {
						values.push(applyOperation(operations.pop(), values.pop(), values.pop()));
					}
				}
				if (!operations.isEmpty()) {
					if (operations.peek().equals("(")) {
						operations.pop();
					}
				}
			}

			else if (token.equals("]")) {
				while (!(operations.peek().equals("["))) {
					{
						values.push(applyOperation(operations.pop(), values.pop(), values.pop()));
					}
				}
				if (!operations.isEmpty()) {
					if (operations.peek().equals("[")) {
						operations.pop();
					}
				}
				String arName = operations.peek();
				int index = arrays.indexOf(new Array(arName));
				if (index > -1) {
					operations.pop();
					Array a1 = arrays.get(index);
					int indexOfa1 = (int) Math.round(values.pop());
					float val = a1.values[indexOfa1];
					values.push(val);
				}
			}

			else if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
				while (!operations.isEmpty() && hasPrecedence(token, operations.peek())) {
					{
						values.push(applyOperation(operations.pop(), values.pop(), values.pop()));
					}

				}
				operations.push(token);
			}

			else if (arrays.contains(new Array(token))) {
				operations.push(token);
			}

			else {
				Variable var = new Variable(token);
				if (vars.contains(var)) {
					int indexOfVariable = vars.indexOf(var);
					float val = vars.get(indexOfVariable).value;
					values.push(val);
				}
			}

		}

		while (!operations.isEmpty() && values.size() > 1) {
			values.push(applyOperation(operations.pop(), values.pop(), values.pop()));
		}

		return (values.pop());

	}

	private static boolean hasPrecedence(String operation1, String operation2) {
		if (operation2.equals("(") || operation2.equals("") || operation2.equals("[") || operation2.equals("]")) return false;
		if ((operation1.equals("*") || (operation1.equals("/")) && (operation2.equals("+")) || operation2.equals("-"))) return false;
		else return true;
	}

	private static float applyOperation(String operation, float b, float a) {
		if (operation.equals("+")) return a + b;
		else if (operation.equals("-")) return a - b;
		else if (operation.equals("*")) return a * b;
		else if (operation.equals("/")) return a / b;

		return 0;
	}

	private static boolean isNumeric(String s) {
		try {
			int number = Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}