package app;
//(A[A-abc]+a[a-A])*(ABC[a-abc]-abc[A-ABC])/ABC[(10/2)-(12/3)]+a[(9/3)-(6-5)]-13
// abc[a]+a[abc]+A[abc]+ABC[a]+abc[A]+A[ABC]+a[ABC]+ABC[A[abc]]
import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression
{

	public static String delims = " \t*+-/()[]";

	private static ArrayList ConverttoArray(String expr)
	{
		expr = expr.replaceAll("\\s","");
		ArrayList<Object> List = new ArrayList<Object>();
		for(int location = 0; location<expr.length();/*location++*/)
		{
			boolean number = false;
			boolean name = false;
			String addMe = "";
			while ((location<expr.length()) && (expr.charAt(location)>47 && expr.charAt(location)<58))
			{
				number = true;
				addMe+=expr.charAt(location);
				location++;
			}
			if(number)
			{
				//System.out.println(addMe);
				List.add(Float.parseFloat(addMe));
				continue;
			}
			while((location<expr.length()) && ((expr.charAt(location)>64 && expr.charAt(location)<91) || (expr.charAt(location)>96 && expr.charAt(location)<123)))  //while it is a letter
			{
				name=true;
				addMe += expr.charAt(location);
				location++;
			}
			if(name)
			{
				//System.out.println(addMe);
				List.add((String)addMe);
				continue;
			}
			List.add(expr.charAt(location));
			location++;
		}
		System.out.println("Converted: " + List);
		return List;
	}

	/**
		* Populates the vars list with simple variables, and arrays lists with arrays
		* in the expression. For every variable (simple or array), a SINGLE instance is created
		* and stored, even if it appears more than once in the expression.
		* At this time, values for all variables and all array items are set to
		* zero - they will be loaded from a file in the loadVariableValues method.
		*
		* @param expr The expression
		* @param vars The variables array list - already created by the caller
		* @param arrays The arrays array list - already created by the caller
	*/
  public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays)
	{
		expr = expr.replaceAll("\\s","");
		//System.out.println(expr);
		for(int location = 0; location<expr.length();location++)
		{
		//	System.out.println(expr.charAt(location));
			if(!((expr.charAt(location)>64 && expr.charAt(location)<91) || (expr.charAt(location)>96 && expr.charAt(location)<123)))
				continue;
			String name = "";
			while(location<expr.length() && ((expr.charAt(location)>64 && expr.charAt(location)<91) || (expr.charAt(location)>96 && expr.charAt(location)<123))) //while it is a letter
			{
				name += expr.charAt(location);
				location++;
			}
			//System.out.println(name);
			if(location<expr.length() && expr.charAt(location)=='[' && !(arrays.contains(new Array(name))))
			{
				arrays.add(new Array(name));
			}
			else if(!(vars.contains(new Variable(name))))
			{
				vars.add(new Variable(name));
			}
		}
		System.out.println("Expression: "+ expr+ "\nVariables: "+ vars + "\nArrays: " + arrays);
  }

  /**
     * Loads values for variables and arrays in the expression
     *
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
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
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;
                }
            }
        }
    }

	private static ArrayList<Object> Replace(ArrayList<Object> expression, ArrayList<Variable> vars, ArrayList<Array> arrays)
	{
		for(int item = 0; item < expression.size();item++)
		{
			boolean array=false;
			if(item+3<expression.size() )
			{
				if(expression.get(item+1) instanceof Character && (char)expression.get(item+1)=='[')
					array=true;
				if(expression.get(item) instanceof String && expression.get(item+1) instanceof Character && expression.get(item+2) instanceof Float && expression.get(item+3) instanceof Character)
				{
					String part1 = (String) expression.get(item);
					char part2 = (char) expression.get(item+1);
					// System.out.println(expression.get(item));
					// System.out.println(expression.get(item+1));
					// System.out.println(expression.get(item+2));
					// System.out.println(expression.get(item+3));
					float temp = (Float) expression.get(item+2);
					int part3 = (int) temp;
					char part4 = (char) expression.get(item+3);
					if(part2==('[') && part4==(']'))
					{
						array = true;
						for(int place = 0; place<arrays.size(); place++)
						{
							if(arrays.get(place).name.equals(part1))
							{
								expression.set(item,(float)arrays.get(place).values[part3]);
								expression.remove(item+1);
								expression.remove(item+1);
								expression.remove(item+1);
							}
						}
					}
				}
			}
			if(expression.get(item) instanceof String && array==false)
			{
				for(int place = 0; place<vars.size(); place++)
				{
					if(vars.get(place).name.equals(expression.get(item)))
						expression.set(item,(float)vars.get(place).value);
				}
			}
		}
		//System.out.println("Substituted: " + expression);
		return expression;
	}

	private static float RecEval(ArrayList<Object> expression, ArrayList<Variable> vars, ArrayList<Array> arrays)
	{
		//System.out.println("Recursive Eval recieved: " + expression);
		expression = Replace(expression, vars, arrays);
// 		try
// {
//     Thread.sleep(1000);
// }
// catch(InterruptedException ex)
// {
//     Thread.currentThread().interrupt();
// }
		//if only one number left
		if(expression.size()==1)
		{
			//System.out.println("returning: " + expression.get(0));
			Float returnMe = (Float) expression.get(0);
			return returnMe;
		}
		//evaluating parantheses
		int start = expression.indexOf('(');
		int end = start+1;
		if(start!=-1)
		{
		  for(int count=1;count>0;end++)
		  {
				if(expression.get(end) instanceof Character)
				{
			    if((Character)expression.get(end)==')')
			      count--;
			    else if((Character)expression.get(end)=='(')
			      count++;
				}
		  }
			end = end -1;
			int length = end-start-1;
			expression.remove(start);
			end=end-1;
			expression.remove(end);
			ArrayList<Object> MiddleBits = new ArrayList<Object>(expression.subList(start,end));
			float middle = RecEval(MiddleBits,vars,arrays);
		  expression.subList(start, end).clear();
			expression.add(start, middle);
			return RecEval(expression,vars,arrays);
		}
		//evaluate Brackets
		start = expression.indexOf('[');
		end = start+1;
		if(start!=-1)
		{
		  for(int count=1;count>0;end++)
		  {
				if(expression.get(end) instanceof Character)
				{
			    if((Character)expression.get(end)==']')
			      count--;
			    else if((Character)expression.get(end)=='[')
			      count++;
				}
		  }
			end = end -1;
			int length = end-start-1;
			ArrayList<Object> MiddleBits = new ArrayList<Object>(expression.subList(start+1,end));
			float middle = RecEval(MiddleBits,vars,arrays);
		  expression.subList(start+1, end).clear();
			expression.add(start+1, middle);
			return RecEval(expression,vars,arrays);
		}
		//evaluate division and multiplication
		int firstDivide = expression.indexOf('/');
		int firstMultiply = expression.indexOf('*');
		//division
		if((firstDivide!=-1 && firstDivide < firstMultiply) || (firstMultiply==-1 && firstDivide!=-1))
		{
			start = firstDivide-1;
			end = firstDivide+1;
			int length = end-start-1;
			float middle = ((Float)expression.get(start))/((Float)expression.get(end));
		  expression.subList(start, end+1).clear();
			expression.add(start, middle);
			return RecEval(expression,vars,arrays);
		}
		//multiplication
		if((firstMultiply!=-1 && firstMultiply < firstDivide) || (firstDivide==-1 && firstMultiply!=-1))
		{
			start = firstMultiply-1;
			end = firstMultiply+1;
			int length = end-start-1;
			float middle = ((Float)expression.get(start))*((Float)expression.get(end));
		  expression.subList(start, end+1).clear();
			expression.add(start, middle);
			return RecEval(expression,vars,arrays);
		}
		//evaluate addition and subtraction
		int firstAddition = expression.indexOf('+');
		int firstSubtraction = expression.indexOf('-');
		//Additon
		if((firstAddition!=-1 && firstAddition < firstSubtraction) || (firstSubtraction==-1 && firstAddition!=-1))
		{
			start = firstAddition-1;
			end = firstAddition+1;
			int length = end-start-1;
			float middle = ((Float)expression.get(start))+((Float)expression.get(end));
		  expression.subList(start, end+1).clear();
			expression.add(start, middle);
			return RecEval(expression,vars,arrays);
		}
		//Subtraction
		if((firstSubtraction!=-1 && firstSubtraction < firstAddition) || (firstAddition==-1 && firstSubtraction!=-1))
		{
			start = firstSubtraction-1;
			end = firstSubtraction+1;
			int length = end-start-1;
			float middle = ((Float)expression.get(start))-((Float)expression.get(end));
		  expression.subList(start, end+1).clear();
			expression.add(start, middle);
			return RecEval(expression,vars,arrays);
		}
		System.out.println("SHOULD NOT BE HERE");
		return 1;
	}
  /**
   * Evaluates the expression.
   *
   * @param vars The variables array list, with values for all variables in the expression
   * @param arrays The arrays array list, with values for all array items
   * @return Result of evaluation
   */
  public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays)
	{
		expr = expr.replaceAll("\\s","");
		System.out.println("OG Evaluate recieved: " + expr);
		ArrayList<Object> expression = Replace(ConverttoArray(expr), vars, arrays);
		return RecEval(expression, vars, arrays);
  }
}
