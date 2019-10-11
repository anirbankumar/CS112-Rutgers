package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		Node addedNode =  new Node(0, 0 , null);
		int exponent = 0;
		float coefficent = 0;

		while(poly1 != null || poly2 != null){
			if(poly1 != null && poly2 == null){
				exponent = poly1.term.degree;
				coefficent = poly1.term.coeff;

				poly1 = poly1.next; // increment poly1
			}
			else if(poly1 == null && poly2 != null){
				exponent = poly2.term.degree;
				coefficent = poly2.term.coeff;

				poly2 = poly2.next;
			}
			else if(poly1.term.degree == poly2.term.degree) { // same degree
				exponent = poly1.term.degree;
				coefficent = poly1.term.coeff + poly2.term.coeff;

				poly1 = poly1.next;
				poly2 = poly2.next;
			}
			else if(poly1.term.degree < poly2.term.degree){
				exponent = poly1.term.degree;
				coefficent = poly1.term.coeff;

				poly1 = poly1.next;
			}
			else if(poly1.term.degree > poly2.term.degree){
				exponent = poly2.term.degree;
				coefficent = poly2.term.coeff;

				poly2 = poly2.next;
			}

			addedNode = new Node(coefficent, exponent, addedNode);
		}


		Node previousNode = null;
		Node nextNode;
		
		while(addedNode != null) {
			nextNode = addedNode.next;
			addedNode.next = previousNode;
			previousNode = addedNode;
			addedNode = nextNode;
		}
		previousNode = previousNode.next;


		return previousNode;
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		Node newPoly1 = poly1;
		Node newPoly2 = poly2;
		
		Node frnt = null;
		Node last = null;
		
		Node combinedNodes = null;
		
		while (newPoly1 != null) {

			if (newPoly2 == null) {
				newPoly2 = poly2; // resets back to beginning
			}
			while (newPoly2 != null) {
				Node ptr = new Node((newPoly1.term.coeff * newPoly2.term.coeff), (newPoly1.term.degree + newPoly2.term.degree), null);
				combinedNodes = add(combinedNodes, ptr); //simplification if needed
				if (last != null) {
					last.next = ptr;
				} else {
					frnt = ptr;
				}
				last = ptr;
				newPoly2 = newPoly2.next;
			}
			newPoly1 = newPoly1.next;
		}

		return combinedNodes;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		float result = 0;
		while(poly != null) {
			result += (poly.term.coeff * ((float)Math.pow(x, poly.term.degree)));
			poly = poly.next;
		}

		return result;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ; current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
