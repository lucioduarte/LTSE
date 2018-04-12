/**
 * 
 */
package ltse.context;

import java.util.Stack;

import ltse.LTSExtractor;
import ltse.file.Definitions;

/**
 * Class that implements the control over the stack of method calls.
 * 
 * @author Lucio Mauro Duarte
 */

public class CallStack implements Definitions {
	private Stack<String> calls;
		
	public CallStack () {
		calls = new Stack<String> ();
	}
	
	/**
	 * Returns whether the current stack is empty or not
	 * 
	 * @return <code> true </code> if the current stack is empty and 
	 * <code> false </code>, otherwise
	 * 
	 * @throws NullPointerException
	 */
	public boolean isEmpty () throws NullPointerException {
		try {
			boolean ret;
			ret = calls.isEmpty ();
			return ret;
		}
		catch (NullPointerException e) {
		  throw e;
		}
	}
	
	/**
	 * Returns the current call stack.
	 * 
	 * @return the current call stack
	 * 
	 * @throws NullPointerException
	 */
	public Stack<String> getCallStack () throws NullPointerException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getCallStack()");
		}
		
		try {
			return calls;
		}
		catch (NullPointerException e) {
			throw e;
		}
	}

	/**
	 * Returns the current context, i.e., the one at the the top of the stack
	 * 
	 * @return the currentContext or <code> null </code>, in case the stack is empty
	 * 
	 * @throws NullPointerException
	 */
	public String getCurrentCall () throws NullPointerException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getCurrentCall()");
		}
		
		try {
			boolean empty;
			empty = !isEmpty ();
			if (empty) {
				String ret;
				ret = calls.peek ();
				return ret;
			}
			else return null;
		}
		catch (NullPointerException e) {
		  throw e;
		}
	}
		
	/**
	 * Pushes a new context into the context stack
	 * 
	 * @param ctx the new context to be pushed into the stack
	 * 
	 * @throws NullPointerException
	 */
	public void pushCall (String ctx) throws NullPointerException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".pushCall("+ctx+")");
		}
		
		try {
			calls.push (ctx);
		}
		catch (NullPointerException e) {
		  throw e;
		}
	}
		
	/**
	 * Returns the context at the top of the stack, removing it
	 * 
	 * @return the context at the top of the stack or <code> null </code> if the 
	 * stack is empty
	 * 
	 * @throws NullPointerException
	 */
	public String popCall () throws NullPointerException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".popCall()");
		}
		
		try {
			boolean empty;
			empty = !isEmpty ();
			if (empty) {
				String ret;
				ret = calls.pop ();
				return ret;
			}
			else return null;
		}
		catch (NullPointerException e) {
		  throw e;
		}
	}	
	
	/**
	 * Creates a string containing the contents of the call stack.
	 * 
	 * @return the string with the contents of the call stack
	 * 
	 * @throws NullPointerException
	 */
	public String getCalls () throws NullPointerException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getCalls()");
		}
		
		java.util.Iterator<String> i;
		
		try {
			i = calls.iterator ();
		}
		catch (NullPointerException e) {
		  throw e;
		}
		
		String c = new String ();
		c += "<";
		boolean hasnext;
		hasnext= i.hasNext();
		while (hasnext) {
			c += i.next ();
			boolean aux;
			aux = i.hasNext();
			if (aux) {
				c += ", ";
			}
			hasnext = i.hasNext();
		}
		c += ">";
		
		return c;
	}	
}

