package ltse.context;

/**
 * Class that annotates log files with state numbers according
 * to context information.
 *
 * @author Lucio Mauro Duarte
 * @version 15/08/2017
 */
 


import java.io.*;
import java.util.*;
import ltse.LTSExtractor;
import ltse.file.Definitions;
import ltse.refine.StateRefiner;
import ltse.filter.InterestFilter;

/**
 * Annotate a log file with context information.
 */
public class ContextAnnotator implements Definitions {

	/** Buffer used to read information from the input file */
	private BufferedReader in;
	/** Buffer used to write information to the output file */
	private BufferedWriter out;
	/** Table used to store context information */
	private ContextTable table;
	/** Context stack */
	private ContextStack cs;
	/** Call stack */
	private CallStack calls;
	/** Current state information */
	private int currentState;
	/** State refiner */
	private StateRefiner refiner;
	/** Filter of actions */
	private InterestFilter filter;
	/** Name of the model to be created */
	private String name;
	/** The mode selected to generate the model: call, termination or entry/exit */
	private int mode;
	/** Indicates whether there is any action between contexts */
	private boolean actionOccurred;
	
	/**
	 * Creates a new context annotator with an empty context table.
	 *
	 * @param r the state refiner used to define different states.
	 * @param f the filter used to define the alphabet of the model.
	 * @param m the mode used to build the model.
	 */
	public ContextAnnotator (StateRefiner r, InterestFilter f, int m) {
		this.refiner = r;
		this.filter = f;
		this.mode = m;
		this.cs = new ContextStack ();
		this.calls = new CallStack ();
		this.currentState = -1;
		this.table = new ContextTable ();
		this.actionOccurred = false;
	}
	
	/**
	 * Creates a new context annotator using an existing context table.
	 *
	 * @param r the state refiner used to define different states.
	 * @param f the filter used to define the alphabet of the model.
	 * @param m the mode used to build the model.
	 * @param ct an existing context table
	 */
	public ContextAnnotator (StateRefiner r, InterestFilter f, int m, ContextTable ct) {
		this.refiner = r;
		this.filter = f;
		this.mode = m;
		this.cs = new ContextStack ();
		this.calls = new CallStack ();
		this.table = ct;
		this.currentState = -1;
		this.actionOccurred = false;
	}


/*******************************************************************************/
	
	/**
	 * Sets the current context table using a previously created
	 * table.
	 *
	 * @param t a previously created context table.
	 */
	public void setContextTable (ContextTable t) {
		this.table = t;
	}

/*******************************************************************************/
	
	/**
	 * Sets the name of the current model
	 * 
   * @param name the name to set for the current model
   */
  public void setName(String name) {
	  this.name = name.trim();
  }

 /*******************************************************************************/
  
	/**
	 * Returns the name of the current model
	 * 
   * @return the name of the current model
   */
  public String getName() {
	  return this.name.trim();
  }

/*******************************************************************************/
  
	/**
	 * Creates a context table and annotate an input file with context information.
	 *
	 * @param logs list of log files used as inputs
	 * @param name the name of the model
	 * @param ctxFiles reference to list of context files to be generated
	 *
	 * @return the created context table
	 */
	public ContextTable createContextTable (LinkedList<String> logs, String name,
	                                        LinkedList<String> ctxFiles)
		                                      throws ContextAnnotationException {

		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".createContextTable("+logs+","+name+","+ctxFiles+")");
		}
		ListIterator<String> l = logs.listIterator ();

		try {
			int counter = 1;

			if (!l.hasNext ())
				throw new ContextAnnotationException ("Error: No log file provided!!");

			// Processes each log file
			while (l.hasNext ()) {

				String fileName = l.next ();
				String contextFile = name + "_" + counter;
				counter++;

				String ctxName = contextFile + "."+ CTX_EXT;
				// Creates buffer to write to file
				FileWriter w = new FileWriter (ctxName);
				this.out = new BufferedWriter (w);

				//System.out.println ("ctxName = " + ctxName);
				
				ctxFiles.add (ctxName);
				ltse.LTSExtractor.tempLogs.add(ctxName);
				
				// Resets the call stack
				this.calls = new CallStack ();
				// Resets context stack
				this.cs = new ContextStack ();
				// Reset current state
				this.currentState = -1;
				// Resets action occurrence
				this.actionOccurred = false;

				// Adds global context to context table
				//System.out.println ("Adds global context");
				updateCurrentContext (GLOBAL, "true", "{}", GLOBAL_ID);

				// Creates buffer to read from file
				FileReader r = new FileReader (fileName);
				this.in = new BufferedReader (r);

				// Creates a context annotation in the output file
				// for each annotation found in the input file
				String entry = new String ();
				boolean more = true;
				while ((entry != null) && more) {
					entry = in.readLine ();
					
					if (LTSExtractor.fullDebugOn) {
						System.out.println ("\nEntry = " + entry);
					}
					
					if (entry != null) {
						more = createAnnotation (entry);
					}
				}
				
				// Closes and reset buffers
				this.in.close ();
				this.in = null;
				this.out.close ();
				this.out = null;
			}
		}
		catch (IOException e) {
			throw new ContextAnnotationException (e.getMessage ());
		}

		return this.table;
	}

/*******************************************************************************/
	
	/**
	 * Updates the current context, adding a new entry to the context table if it 
	 * is a new context or recovering context information if it is a context 
	 * already in the context table. Returns the context ID of the current 
	 * context.
	 *
	 * @param t	 the predicate associated to the context
	 * @param v the value of the predicate
	 * @param a the list of attributes for this context
	 * @param id the ID of the structure which originated the context
	 *
	 * @return the numeric ID of the context in the context table
	 */
	private void updateCurrentContext (String t, String v, String a, int id) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".updateCurrentContext("+t+","+v+","+a+","+id+")");
		}
		
		// Recovers the current list of calls in the call stack
		String c = this.calls.getCalls();
		
		// If context is already in the table, uses the same context info
		if (this.table.isInTable (t, v, a, id, c)) {
			if (LTSExtractor.fullDebugOn) {
				System.out.println ("===> in table");
			}
			
			// Retrieves known state
			int newState = this.table.getState (v, a, id, c);
			if (LTSExtractor.fullDebugOn) {
				System.out.println ("===> state = " + newState);
			}
				
			this.currentState = newState;
		}
		// Otherwise, obtains new context information
		else {
			if (LTSExtractor.fullDebugOn) {
				if (t.equals (GLOBAL)) {
					System.out.println ("New GLOBAL!");
				}
			}
			//System.out.println ("===> NOT in table");
			this.currentState = this.table.getNextState ();
			// Adds new entry to the table
			this.table.add (this.currentState, t, v, id, a, c);
		}
		
		if (LTSExtractor.fullDebugOn) {
			System.out.println ("currentState = " + currentState);
		}
		
		// CHANGED
		// Pushes current context into the context stack
		this.cs.pushContext (t);
		// Adds current context to the context trace
		addContextToTrace ();
		
		// If an action has occurred between contexts or they 
		// are different contexts
		if (this.actionOccurred || 
				!(this.cs.getCurrentContext ().equals (t))) {
			
			if (this.actionOccurred) {
				this.actionOccurred = false;
			}
		}

		//if (cs.isEmpty () || cs.getCurrentContext ().equals (GLOBAL)) {
		// If it is a global context, adds it to the context trace
		//if (currentContextIsGlobal ()) {
						
			// Pushes current context into the context stack
		//this.cs.pushContext (t);
			// Adds current context to the context trace
			//addContextToTrace ();
		//}
		//else {
			// If an action has occurred between contexts or they 
			// are different contexts
			//if (this.actionOccurred || 
				//	!(this.cs.getCurrentContext ().equals (t))) {
				
			//	if (this.actionOccurred) {
				//	this.actionOccurred = false;
			//	}
				//else System.out.println ("===> not the same context");
				// Pushes current context into the context stack
				//this.cs.pushContext (t);
				// Adds current context to the context trace
				//addContextToTrace ();
			//}
		//}
	}
	
	/**
	 * Adds the current context, indicated by the current state, to
	 * the context trace file.
	 */
	private void addContextToTrace () {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".addContextToTrace()");
		}
		
		try {
			this.out.write (SEP + currentState);
			
			if (LTSExtractor.fullDebugOn) {
				System.out.println ("writing = " + SEP + currentState);
			}
			
			this.out.newLine ();
			this.out.flush ();
		}
		catch (Exception e) {
			System.out.println("Error to context trace file: " + e.getMessage ());
		}
	}

/*******************************************************************************/
	
	/**
	 * Creates a context annotation depending on the type of statement
	 *
	 * @param event the event which the annotation will be created for
	 */
	private boolean createAnnotation (String entry) throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".createAnnotation("+entry+")");
		}
		String aux = entry;

		// Obtains type of event
		int i = aux.indexOf (LABEL_SEP);

		if (i > 0) {
			String type = aux.substring (0, i);
			//System.out.println("Type = " + type);
			aux = aux.substring (i+1);

			// Beginning of selection and repetition statements
			if (type.equals (SEL_ENTER) || type.equals (REP_ENTER)) {
				statementEnter (type, aux);
				return true;
			}
			else
				// End of selection and repetition statements
				if (type.equals (SEL_END) || type.equals (REP_END)) {
					statementExit (aux);
					return true;
				}
				else
					// Beginning of method calls
					if (type.equals (CALL_ENTER))
						return callEnter (aux);
					else
						// End of method calls
						if (type.equals (CALL_END))
							return callExit (aux);
						// Beginning of internal method calls
						else 
							if (type.equals (INT_CALL_ENTER))
								return intCallEnter (aux);
							else
								// End of internal method calls
								if (type.equals (INT_CALL_END))
									return intCallExit (aux);
			
								else
									// Beginning of execution of methods
									if (type.equals (MET_ENTER))
										return methodEnter (aux);
									else
										// End of execution of methods
										if (type.equals (MET_END))
											return methodExit (aux);
										else
											// User-defined events
											if (type.equals (ACTION)) {
												actionExecution (aux, "");
												return true;
											}
		}
		/*else
			if (entry.equals (END_STATE)) {
				out.write (SEP + entry);
				out.flush ();
				return true;
			}*/

		return false;
	}

/*******************************************************************************/
	
	/**
	 * Handles annotations concerning the entry of a selection or repetition statement.
	 * 
	 * @param type the type of the annotation
	 * @param aux the annotation itself
	 */
	private void statementEnter (String type, String aux) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".statementEnter("+type+","+aux+")");
		}

		/* OBTAINS INFORMATION */
		
		int i = aux.indexOf (SEP);
		// Obtains the description of the test associated to the statement
		String test = aux.substring (0, i).trim();
		
		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the evaluation of the test
		String value = aux.substring (0, i).trim();
				
		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the object which executed the statement
		@SuppressWarnings("unused")
    String object = aux.substring (0, i).trim();
		aux = aux.substring (i+1);
		
		i = aux.indexOf ("{");
		int j = aux.lastIndexOf (SEP);
		String attr = aux.substring (i+1, j).trim();
		
		aux = aux.substring (j+1);
		// Obtains ID
		int	id = Integer.parseInt (aux);
		
		// Obtains list of attributes
		String attribs = collectAttributes (attr).trim();

		/* APPLIES ACTIONS */

		// Adds entry to table
		updateCurrentContext (test.trim(), value.trim(), attribs.trim(), id);
	}

/*******************************************************************************/
	
	/**
	 * Handles annotations concerning the exit of a selection or repetition statement.
	 */
	private void statementExit (String aux) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".statementExit("+aux+")");
		}
		// Restores previous context
		int i = aux.indexOf (SEP);
		// Obtains the description of the test associated to the statement
		String test = aux.substring (0, i).trim();
		
		// Verifies that the context being removed from the stack is the
		// correct one
		String currentContext = cs.popContext ().trim();
		assert currentContext.equals (test.trim());
	}
	
/*******************************************************************************/
	
	/**
	 * Handles annotations concerning the entry of an internal method call.
	 * 
	 * @param aux the annotation
	 * @return returns true for a successful execution
	 * @throws IOException
	 */
	private boolean intCallEnter (String aux) throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".intCallEnter("+aux+")");
		}
		//System.out.println("intCallEnter = " + aux);

		/* OBTAINS INFORMATION */

		int i = aux.indexOf (SEP);
		// Obtains the name of the method being called
		String method = aux.substring (0, i).trim();
		//System.out.println("Method = " + method);

		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the ID of the object whose the method was called
		String callee = aux.substring (0, i).trim();
		//System.out.println("Callee = " + callee);
		
		// Obtains the callee class name
		String calleeClassName = callee.trim();
		int k = callee.indexOf("=");
		if (k > 0)
			calleeClassName = callee.substring(0, k).trim();
		//System.out.println("calleeClassName = " + calleeClassName);

		// Obtains string of attributes
		aux = aux.substring (i+1);
		i = aux.indexOf ("{");
		int l = aux.lastIndexOf (SEP);
		String attr = aux.substring (i+1, l).trim();
		//System.out.println("Attr = " + attr);

		aux = aux.substring (l+1);
		// Obtains ID
		int	id = Integer.parseInt (aux);
		//System.out.println("Id = " + id);

		// Obtains list of attributes
		String attribs = collectAttributes (attr).trim();
		//System.out.println("Attributes = " + attribs);

		String callingName = "call" + "." + calleeClassName.trim() + "." + method.trim();
		// Adds entry to context table
		updateCurrentContext (callingName.trim(), "true", attribs.trim(), id);
		// Adds the call to the call stack
		this.calls.pushCall(callingName.trim());

		//if (!calleeClassName.equals (callerClassName)) {
						
			/* APPLIES ACTIONS */
			String actionName = "call" + "." + method.trim(); 
			//System.out.println("actionName = " + actionName);
			
			String suffix = "";
			// If the mode is entry and exit, adds the necessary suffix
			if (this.mode == ENTRY_AND_EXIT)
				suffix += ENTER_SUFFIX;

			// If the current mode requires the method call entry as an action...
			if (this.mode == CALL || mode == ENTRY_AND_EXIT) {
				// If the action name is in the filter list or there is no filter to be applied,
				// then writes it on the context file
				if ((this.filter.hasActionsOfInterest () && this.filter.isActionOfInterest (actionName.trim()))
			 	 		|| (!this.filter.hasActionsOfInterest ())) {
					
					// Writes only the method name to the context file with appropriate suffix
					this.out.write (actionName.trim() + suffix.trim());
					this.out.newLine ();
					this.out.flush ();
					
					// Sets that an action has occurred, used to control transitions between
					// contexts
					this.actionOccurred = true;
				}
			}
		//}
		return true;
	}
	
/*******************************************************************************/
	
	/**
	 * Handles annotations concerning the exit of an internal method call.
	 * 
	 * @param aux the annotation
	 * @return returns true for a successful execution
	 * @throws IOException
	 */
	private boolean intCallExit (String aux)	throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".intCallExit("+aux+")");
		}
		//System.out.println("intCallExit = " + aux);

		/* OBTAINS INFORMATION */

		int i = aux.indexOf (SEP);
		// Obtains the name of the method being called
		String method = aux.substring (0, i).trim();
		//System.out.println("Method = " + method);

		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the ID of the object whose the method was called
		String callee = aux.substring (0, i).trim();
		//System.out.println("Callee = " + callee);
		
		// Obtains the callee class name
		String calleeClassName = callee.trim();
		int k = callee.indexOf("=");
		if (k > 0)
			calleeClassName = callee.substring(0, k).trim();
		//System.out.println("calleeClassName = " + calleeClassName);

		/* APPLIES ACTIONS */
	

		//if (!calleeClassName.equals (callerClassName)) {
			String actionName = "call" + "." + method.trim();

			String suffix = "";
			if (this.mode == ENTRY_AND_EXIT)
				suffix += EXIT_SUFFIX;

			// If necessary, writes method name to context file
			if (this.mode == TERMINATION || mode == ENTRY_AND_EXIT) {
				if ((this.filter.hasActionsOfInterest () && this.filter.isActionOfInterest (actionName.trim()))
						|| (!this.filter.hasActionsOfInterest ())) {

					// Writes only the method name to the context file with appropriate suffix
					this.out.write (actionName.trim() + suffix.trim());
					this.out.newLine ();
					this.out.flush ();	
					
					// Sets that an action has occurred, used to control transitions between
					// contexts
					this.actionOccurred = true;
				}
			}
		//}

		// Creates call name
		String callingName = "call" + "." + calleeClassName.trim() + "." + method.trim();
		
		// Verifies that the context being removed from the stack is the
		// correct one
		String currentContext = this.cs.popContext ().trim();
		assert currentContext.equals (callingName.trim());
		
		// Removes the corresponding call from the call stack
		this.calls.popCall();

		return true;
	}
	
/*******************************************************************************/

	/**
	 * Handles annotations concerning the entry of a method call.
	 * 
	 * @param aux the annotation
	 * @return returns true for a successful execution
	 * @throws IOException
	 */
	private boolean callEnter (String aux) throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".callEnter("+aux+")");
		}
		//System.out.println("callEnter = " + aux);

		/* OBTAINS INFORMATION */

		int i = aux.indexOf (SEP);
		// Obtains the name of the method being called
		String method = aux.substring (0, i).trim ();
		//System.out.println("Method = " + method);

		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the ID of the object which called the method
		String caller = aux.substring (0, i).trim ();
		//System.out.println("Caller = " + caller);
		
		// Obtains the caller class name
		int j = caller.indexOf(CLASS_SEP);
		String callerClassName = caller.substring(0, j).trim ();
		//System.out.println("callerClassName = " + callerClassName);

		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the ID of the object whose the method was called
		String callee = aux.substring (0, i).trim ();
		//System.out.println("Callee = " + callee);
		
		// Obtains the callee class name
		String calleeClassName = generateCalleeClassName (callee).trim ();
		//System.out.println("calleeClassName = " + calleeClassName);

		// Obtains string of attributes
		aux = aux.substring (i+1);
		i = aux.indexOf ("{");
		int l = aux.lastIndexOf (SEP);
		String attr = aux.substring (i+1, l).trim ();
		//System.out.println("Attr = " + attr);

		aux = aux.substring (l+1);
		// Obtains ID
		int	id = Integer.parseInt (aux);
		//System.out.println("Id = " + id);

		// Obtains list of attributes
		String attribs = collectAttributes (attr).trim ();
		//System.out.println("Attributes = " + attribs);

		String callingName = calleeClassName.trim () + "." + method.trim ();
		// Adds entry to context table
		updateCurrentContext (callingName.trim (), "true", attribs.trim (), id);
		// Adds the call to the call stack
		this.calls.pushCall(callingName.trim ());

		if (!calleeClassName.trim ().equals (callerClassName.trim ())) {
						
			/* APPLIES ACTIONS */
			String actionName = calleeClassName.trim ()/*.toLowerCase()*/ + "." + method.trim (); 
			//System.out.println("actionName = " + actionName);
			
			String suffix = "";
			// If the mode is entry and exit, adds the necessary suffix
			if (this.mode == ENTRY_AND_EXIT)
				suffix += ENTER_SUFFIX;

			// If the current mode requires the method call entry as an action...
			if (this.mode == CALL || mode == ENTRY_AND_EXIT) {
				// If the action name is in the filter list or there is no filter to be applied,
				// then writes it on the context file
				if ((this.filter.hasActionsOfInterest () && this.filter.isActionOfInterest (actionName.trim ()))
			 	 		|| (!this.filter.hasActionsOfInterest ())) {
				
					String modifiedCalleeClassName = calleeClassName.trim ().replace('.', '_');
					String modifiedActionName = modifiedCalleeClassName.trim () + "." + method.trim ();
					
					// Writes only the method name to the context file with appropriate suffix
					this.out.write (modifiedActionName.trim () + suffix.trim ());
					this.out.newLine ();
					this.out.flush ();
					
					// Sets that an action has occurred, used to control transitions between
					// contexts	
					this.actionOccurred = true;
				}
			}
		}
		return true;
	}

/*******************************************************************************/
	
	/**
	 * Handles annotations concerning the exit of a method call.
	 * 
	 * @param aux the annotation
	 * @return returns true for a successful execution
	 * @throws IOException
	 */
	private boolean callExit (String aux)	throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".callExit("+aux+")");
		}
		//System.out.println("callExit = " + aux);

		/* OBTAINS INFORMATION */

		int i = aux.indexOf (SEP);
		// Obtains the name of the method being called
		String method = aux.substring (0, i).trim();
		//System.out.println("Method = " + method);

		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the ID of the object which called the method
		String caller = aux.substring (0, i).trim();
		//System.out.println("Caller = " + caller);
		
		// Obtains the caller class name
		int j = caller.indexOf(CLASS_SEP);
		String callerClassName = caller.substring(0, j).trim();
		//System.out.println("callerClassName = " + callerClassName);

		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the ID of the object whose the method was called
		String callee = aux.substring (0, i).trim();
		//System.out.println("Callee = " + callee);
		
		// Obtains the callee class name
		String calleeClassName = generateCalleeClassName (callee.trim()).trim();
		//System.out.println("calleeClassName = " + calleeClassName);

		// Obtains ID
		//int	id = Integer.parseInt (aux);

		/* APPLIES ACTIONS */
	

		if (!calleeClassName.trim().equals (callerClassName.trim())) {
			String actionName = calleeClassName.toLowerCase().trim() + "." + method.trim();

			String suffix = "";
			if (this.mode == ENTRY_AND_EXIT)
				suffix += EXIT_SUFFIX;

			// If necessary, writes method name to context file
			if (this.mode == TERMINATION || this.mode == ENTRY_AND_EXIT) {
				if ((this.filter.hasActionsOfInterest () && this.filter.isActionOfInterest (actionName.trim()))
						|| (!this.filter.hasActionsOfInterest ())) {

					// Writes only the method name to the context file with appropriate suffix
					this.out.write (actionName.trim() + suffix.trim());
					this.out.newLine ();
					this.out.flush ();	
					
					// Sets that an action has occurred, used to control transitions between
					// contexts
					this.actionOccurred = true;
				}
			}
		}

		// Creates call name
		String callingName = calleeClassName.trim() + "." + method.trim();
		
		// Verifies that the context being removed from the stack is the
		// correct one
		String currentContext = cs.popContext ();
		assert currentContext.equals (callingName.trim());
			
		// Removes the corresponding call from the call stack
		calls.popCall();

		return true;
	}

/*******************************************************************************/
	
	/**
	 * Handles annotations concerning the entry of a method body.
	 * 
	 * @param aux the annotation
	 * @return returns true for a successful execution
	 * @throws IOException
	 */
	private boolean methodEnter (String aux) throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".methodEnter("+aux+")");
		}

		/* OBTAINS INFORMATION */

		int i = aux.indexOf (SEP);
		// Obtains the name of the method
		String method = aux.substring (0, i).trim();

		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the object which executed the method
		String object = aux.substring (0, i).trim();
		
	 // Obtains the class name
		i = object.indexOf(CLASS_SEP);
		String className = object.substring(0, i).trim();

		// Identifies the list of attributes
		aux = aux.substring (i+1);
		i = aux.indexOf ("{");
		int j = aux.lastIndexOf (SEP);
		String attr = aux.substring (i+1, j).trim();
		// Obtains the values of the attributes
		String attribs = collectAttributes (attr).trim();

		aux = aux.substring (j+1);
		// Obtains ID
		int	 id = Integer.parseInt (aux);
		
		/* APPLIES ACTIONS */
		String actionName = className.trim() + "." + method.trim();
		
		// Checks whether the execution goes back to a global context and, if so,
		// updates the current context
		//checkAttributesForGlobalContext ();
	
		// If the current context is GLOBAL, checks for any update of attributes
		if (currentContextIsGlobal ()) {
			updateCurrentContext (GLOBAL, "true", attribs.trim(), -1);
		}

		// Adds entry to table
		updateCurrentContext (actionName.trim(), "true", attribs.trim(), id);
		this.calls.pushCall(actionName.trim());

		String suffix = "";
		// If the mode is entry and exit, adds the necessary suffix
		if (this.mode == ENTRY_AND_EXIT)
			suffix += ENTER_SUFFIX;

		// If the current mode requires the method call entry as an action...
		if (this.mode == CALL || this.mode == ENTRY_AND_EXIT) {
			// If the action name is in the filter list or there is no filter to be applied,
			// then writes it on the context file
			if ((this.filter.hasActionsOfInterest () && this.filter.isActionOfInterest (actionName.trim()))
		  		|| (!this.filter.hasActionsOfInterest ())) {
						
				// Writes only the method name to the context file with appropriate suffix
				this.out.write (method.trim() + suffix.trim());
				this.out.newLine ();
				this.out.flush ();
				
				// Sets that an action has occurred, used to control transitions between
				// contexts
				this.actionOccurred = true;
			}
		}
		
		return true;
	}

/*******************************************************************************/
	
	/**
	 * Handles annotations concerning the exit of a method body.
	 * 
	 * @param aux the annotation
	 * @return returns true for a successful execution
	 * @throws IOException
	 */
	private boolean methodExit (String aux)	throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".methodExit.("+aux+")");
		}

		/* OBTAINS INFORMATION */

		int i = aux.indexOf (SEP);
		// Obtains the name of the method
		String method = aux.substring (0, i).trim();
		//System.err.println ("aux_method = " + aux);

		aux = aux.substring (i+1);
		i = aux.indexOf (SEP);
		// Obtains the object which executed the method
		String object = aux.substring (0, i).trim();
		//System.out.println ("aux_object = " + aux);
		
		// Obtains the class name
		i = object.indexOf(CLASS_SEP);
		String className = object.substring(0, i).trim();

		// Identifies the list of attributes
		//aux = aux.substring (i+1);
		//i = aux.indexOf ("{");
		//int j = aux.lastIndexOf (SEP);
		//String attr = aux.substring (i+1, j);
		// Obtains the values of the attributes and sets the 
		// global variable
		//currentAttribs = collectAttributes (attr);

		//aux = aux.substring (i+1);
		// Obtains ID
		//int	 id = Integer.parseInt (aux);

		/* APPLIES ACTIONS */

		// If action mode is set to entry and exit, adds suffix
		String suffix = "";
		if (this.mode == ENTRY_AND_EXIT)
			suffix += EXIT_SUFFIX;
		
		// Creates corresponding action name
		String actionName = className + "." + method;

		// If necessary, writes method name into the context file
		if (this.mode == TERMINATION || this.mode == ENTRY_AND_EXIT)
			if ((this.filter.hasActionsOfInterest () && this.filter.isActionOfInterest (actionName.trim()))
				 || (!this.filter.hasActionsOfInterest ())) {

				// Writes only the method name to the context file with appropriate suffix
				this.out.write (method.trim() + suffix.trim());
				this.out.newLine ();
				this.out.flush ();
				
				// Sets that an action has occurred, used to control transitions between
				// contexts
				this.actionOccurred = true;
			}

		// Verifies that the context being removed from the stack is the
		// correct one
		String currentContext = cs.popContext ().trim();
		assert currentContext.trim().equals (actionName.trim());
			
		// Removes the corresponding call from the call stack
		this.calls.popCall();
		
		//System.out.println("methodExit = " + cs.getCurrentContext());
		
		// If the current context is GLOBAL, checks for any update of attributes
//		if (!cs.isEmpty ()) {
			/*if (currentContext.equals (GLOBAL)){ 
				System.out.println ("***** GLOBAL *****");
				String attribs = table.getAttributes (currentState);
				updateCurrentContext (GLOBAL, "true", attribs.trim(), -1);*/
			//}
		//}
		
		return true;
	}
	
/*******************************************************************************/
	
	/**
	 * Handles annotations concerning actions.
	 * 
	 * @param aux the annotation
	 * @param suffix used to mark an action as an entry or an exit action
	 * @throws IOException
	 */
	private void actionExecution (String aux, String suffix) throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".actionExecution("+aux+","+suffix+")");
		}
		//System.out.println("action");

		/* OBTAINS INFORMATION */

		int i = aux.indexOf (SEP);
		// Obtains the name of the action
		String action = aux.substring (0, i).trim();
		//System.out.println ("action = " + action);
		aux = aux.substring (i+1);
		
		boolean ioAction = false;
		
		// Checks whether it is an input/output action
		int j = action.indexOf("_");
		if (j > 0) {
			String type = action.substring(0, j).trim();
			String value = action.substring(j+1).trim();
			// If it is an input/output action, 
			// creates an appropriate action name,
			// according to the information type
			if (type.equals("in") || type.equals("out")) {
				try {
					Integer n = Integer.parseInt(value);
					action = type.trim () + "[" + n + "]";
				}
				catch (NumberFormatException e1) {
					action = type.trim () + "." + value.trim();
				}
				ioAction = true;
			}
		}

		// Obtains the object which executed the action
		//String object = aux.substring (0);

		/* APPLIES ACTIONS */

		// Writes event name into the context file if action of interest
		if ((filter.hasActionsOfInterest () && filter.isActionOfInterest (action.trim())) ||
			  (!filter.hasActionsOfInterest ()) || ioAction) {
			out.write (action.trim () + suffix.trim());
			out.newLine ();
			out.flush ();
			// Sets that an action has occurred, used to control transitions between
			// contexts
			this.actionOccurred = true;
		}
	}

/*******************************************************************************/
	
	/**
	 * Checks whether attribute values have changed, which signals a change of context.
	 */
	/*private void checkAttributesForGlobalContext () {
		if (LTSExtractor.debugOn) {
			System.out.println(this.getClass().getName() + ".checkAttributesForGlobalContext()");
		}
		// If current context is a state context, check whether it has changed
		if (currentContextIsGlobal ()) {
			System.out.println ("GLOBAL");
			// Obtains the values of attributes for the current context
			String currentAttribs = table.getAttributes (currentState);	
			String c = cs.popContext ();
			//System.out.println ("Pop = " + c);
			System.out.println ("Attribs = " + currentAttribs);
			updateCurrentContext (GLOBAL, "true", currentAttribs, -1);
		}
	}*/

/*******************************************************************************/
	
	/**
	 * Verifies whether the current context is the initial context.
	 * 
	 * @return returns true if the current context is the initial and false otherwise
	 */
	private boolean currentContextIsGlobal () {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".currentContextIsGlobal()");
		}
		
		// If the context stack is empty or the current context is global then 
		// returns true
		if (cs.isEmpty () || cs.getCurrentContext ().equals (GLOBAL)) {
			//System.out.println("It's GLOBAL!");
			return true;
		}

		return false;
	}

/*******************************************************************************/
	
	/**
	 * Obtains the values of attributes necessary for identifying the current context
	 *
	 * @param aux a string containing the list of attributes
	 *
	 * @return the list of values of the necessary attributes
	 */
	private String collectAttributes (String aux) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".collectAttributes("+aux+")");
		}
		//System.out.println("collectAttributes");
		
		String attribs = "{";

		int n = aux.indexOf (ATTR);
		boolean first = true;

		// Obtains set of attributes used for refinement, if necessary
		while (n > 0) {
			String attr = aux.substring (0, n).trim();
			//System.out.println("attr = " + attr);

			int q = attr.indexOf ("=");

			if (q > -1) {
				String name = attr.substring (0, q).trim();
				//System.out.println("name = " + name);
				String val = attr.substring (q+1).trim();
				//System.out.println("value = " + val);

				// If a refinement file has been provided...
				if (refiner.hasStateRefinement ()) {
					// ...and the current attribute is one of the selected attributes...
					if (refiner.isRefinement (name)) {
						// ...then adds attribute to the list
						if (first)
							first = false;
						else
							attribs += ",";

						attribs += name+"="+val;//val;
					}
					else;
				}
				aux = aux.substring (n+1);
				n = aux.indexOf (ATTR);
			}
			//System.out.println("attribs = " + attribs);
		}

		attribs += "}";

		return attribs;
	}
	
	private static String generateCalleeClassName (String callee) {
		String calleeClassName = callee.trim();
		int k = callee.indexOf("@");	
		if (k > 0) {
			String temp = callee.substring(0,k).trim();
			// Changes first character to lower case
			Character first = Character.toLowerCase (temp.charAt (0));
			String rest = temp.substring(1).trim();
			calleeClassName = first + rest.trim();
		}
		return calleeClassName;
	}
}
