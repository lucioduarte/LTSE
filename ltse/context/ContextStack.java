/**
 * Class that implements the control over the stack of contexts.
 * 
 * @author Lucio Mauro Duarte
 * @version 16/02/2011
 */
package ltse.context;

import java.util.Stack;

import ltse.LTSExtractor;
import ltse.file.Definitions;

/**
 * @author particular
 *
 */
public class ContextStack implements Definitions {
	private Stack<String> contexts;
	public String previous;
		
	public ContextStack () {
		contexts = new Stack<String> ();
	}
		
	/**
	 * Returns whether the current context stack is empty or not
	 * 
	 * @return <code> true </code> if the current stack is empty and 
	 * <code> false </code> otherwise
	 */
	public boolean isEmpty () {
		if (LTSExtractor.fullDebugOn && contexts.isEmpty ()) {
			System.out.println("empty!");
		}
		return contexts.isEmpty ();
	}

	/**
	 * Returns the current context, i.e., the one at the the top of the stack
	 * 
	 * @return the currentContext
	 */
	public String getCurrentContext() {
		if (LTSExtractor.fullDebugOn) {
			System.out.println("currentContext: " + contexts.peek());
		}
		
		return contexts.peek ();
	}
		
	/**
	 * Pushes a new context into the context stack
	 * 
	 * @param ctx the new context to be pushed into the stack
	 */
	public void pushContext (String ctx) {
		contexts.push (ctx);
		if (LTSExtractor.fullDebugOn) {
			System.out.println("\npush: " + ctx);
		}
	}
		
	/**
	 * Returns the context at the top of the stack, removing it
	 * 
	 * @return the context of the top of the stack
	 */
	public String popContext () {
		String ctx = contexts.pop ();
		if (LTSExtractor.fullDebugOn) { 
			System.out.println ("\npop: " + ctx);
		}
		return ctx;
	}	
		
	protected String getContexts () {
		java.util.Iterator<String> i = contexts.iterator ();
		String ctxs = new String ();
		ctxs += "<";
		while (i.hasNext()) {
			ctxs += i.next ();
			if (i.hasNext())
				ctxs += ", ";
		}
		ctxs += ">";
		
		return ctxs;
	}	
	
	/*public static void main (String[] args) {
		ContextStack c1 = new ContextStack ();
//		ContextStack c2 = new ContextStack ();
//		ContextStack c3 = new ContextStack ();
//		ContextStack c4 = new ContextStack ();
//		
		c1.pushContext(GLOBAL);
		c1.pushContext("C1");
		c1.pushContext("C2");
		c1.pushContext("C3");
		c1.pushContext("C4");
		
		//c1.showContexts();
		
		c1.popContext(c1.getCurrentContext());
		
		//c1.showContexts();
		
//		c2.pushContext(GLOBAL);
//		c2.pushContext("C1");
//		c2.pushContext("C2");
//		c2.pushContext("C3");
//		
//		c4.pushContext(GLOBAL);
//		c4.pushContext("C1");
//		c4.pushContext("C2");
//		c4.pushContext("C3");
//		c4.pushContext("C4");
//		
//		c3.pushContext(GLOBAL);
//		c3.pushContext("C1");
//		c3.pushContext("C3");
//		c3.pushContext("C2");
//		c3.pushContext("C4");
//		
//		System.out.println("c1 == c2 = " + c1.equals (c2));
//		System.out.println("c1 == c3 = " + c1.equals (c3));
//		System.out.println("c1 == c4 = " + c1.equals (c4));
	}*/
}
