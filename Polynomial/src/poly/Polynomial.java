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
	private static Node reverse(Node original)
	{
		Node newList = null;
		while(original!=null)
		{
			newList = new Node(original.term.coeff, original.term.degree, newList);
			original = original.next;
		}
		return newList;
	}
	public static Node add(Node poly1, Node poly2) {
		Node newList = null;
		float sum = 0;
		int coeff = 0;
		Node start = poly2;
		//System.out.println(poly1.term.degree + " " + poly2.term.degree);
		if(poly1==null)
			return poly2;
		if(poly2==null)
			return poly1;
		while(!(poly1==null && poly2==null))
		{
				if(poly1==null)
				{
					newList = new Node(poly2.term.coeff, poly2.term.degree, newList);
					poly2=poly2.next;
				}
				else if(poly2==null)
				{
					newList = new Node(poly1.term.coeff, poly1.term.degree, newList);
					poly1=poly1.next;
				}
				else if(poly1.term.degree == poly2.term.degree)
				{
					if(poly1.term.coeff + poly2.term.coeff!=0)
						newList = new Node(poly1.term.coeff + poly2.term.coeff, poly1.term.degree, newList);
					poly1=poly1.next;
					poly2=poly2.next;
				}
				else if (poly1.term.degree < poly2.term.degree)
				{
					newList = new Node(poly1.term.coeff, poly1.term.degree, newList);
					poly1=poly1.next;
				}
				else if(poly1.term.degree > poly2.term.degree)
				{
					newList = new Node(poly2.term.coeff, poly2.term.degree, newList);
					poly2=poly2.next;
				}
		}
		return reverse(newList);
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
		Node newList = null;
		int sum = 0;
		float coeff = 0;
		Node start = poly2;
		Node FinalList = null;
		if(poly1 == null)
			return poly1;
		if(poly2==null)
			return poly2;
		while(poly1!=null)
		{
			poly2 = start;
			while(poly2!=null)
			{
				newList = new Node(poly2.term.coeff*poly1.term.coeff, poly2.term.degree + poly1.term.degree, newList);
				poly2=poly2.next;
			}
			poly1=poly1.next;
		}
		int largest = 0;
		for(Node pass = newList; pass!=null; pass = pass.next)
		{
			if(pass.term.degree > largest)
				largest = pass.term.degree;
		}
		//System.out.println(largest);
		Node pass = null;
		for (int currentDeg = largest; currentDeg>=0; currentDeg--)
		{
			pass = newList;
			//sum=0;
			coeff=0;
			while(pass!=null)
			{
				if(pass.term.degree == currentDeg)
				{
					//sum += pass.term.degree;
					coeff += pass.term.coeff;
				}
				pass = pass.next;
			}
			if(coeff!=0)
				FinalList = new Node(coeff, currentDeg, FinalList);
		}
		return FinalList;
	}

	/**
	 * Evaluates a polynomial at a given value.
	 *
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float sum = 0;
		while(poly!=null)
		{
			float temporarySum = 1;
			for(int count=0; count < poly.term.degree; count++)
			{
				temporarySum=temporarySum*x;
			}
			//System.out.println("Temp sum" + temporarySum);
			sum+=temporarySum*poly.term.coeff;
			//System.out.println(sum);
			poly = poly.next;
		}
		return sum;
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
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}
}
