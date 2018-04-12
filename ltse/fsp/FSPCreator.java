package ltse.fsp;

/**
 * Class that implements the generation of FSP specifications based on a state file.
 *
 * @author Lucio Mauro Duarte
 * @version 11/04/2013
 */


import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import ltse.*;
import ltse.file.Definitions;
import ltse.file.FileController;
import ltse.file.OSUtils;
import ltse.model.*;

/**
 * Creates an FSP description from a log file containing state information
 */
 public class FSPCreator implements Definitions {

	/** Structure to store the model to be created */
 	private Model m;
	/** Output stream used to generate the output files */
	private BufferedWriter out;
	/** Flag to indicate that a termination event has been found */
	private boolean ended;
	/** Input stream to load specification */
	private BufferedReader specFile;
	/** Type of model (false for NORMAL and true for PROBABILISTIC) */
	private boolean probabilistic;
	

	/**
	 * Creates a new FSP creator.
	 *
	 * @param name the name of the model to be generated.
	 * @param specFile the name of the file containing the specification for this model.
	 * @param type the type of the model (<code>false</code> for normal and 
	 * <code>true</code> for probabilistic.
	 * 
	 */
	public FSPCreator (String name, BufferedReader specFile, boolean type) {
		out = null;
		ended = false;
		// Creates new model
		m = new Model (name);
		// Inserts initial state
		m.insertState (name);
		this.specFile = specFile;
		this.probabilistic = type;
	}

	/**
	 * Creates a new FSP creator using a loaded model.
	 *
	 * @param lm the loaded model.
	 * @param specFile the name of the file containing the specification for this model.
	 * @param type the type of the model (<code>false</code> for normal and 
	 * <code>true</code> for probabilistic.
	 * 
	 */
	public FSPCreator (Model ml, BufferedReader specFile, boolean type) {
		out = null;
		ended = false;
		m = ml;
		this.specFile = specFile;
		this.probabilistic = type;
	}

	/**
	 * Parses state files and stores information in a data structure
	 *
	 * @param stateFiles The files containing the information used to build the FSP description
	 */
	public Model parseStateFiles (LinkedList<String> ctxFiles)
		throws FSPGenerationException {
		
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".parseStateFiles ("+ctxFiles+")");
		}

		try {

			String currentState = m.getName ();

			int traceCounter = 0;

			// Creates file to record the generated traces, if necessary
			if (LTSExtractor.tracesOn) {
				String traceFile = m.getName () + "."+TRC_EXT;
				FileWriter f = new FileWriter (traceFile);
				out = new BufferedWriter (f);
			}


			// Reads and processes info from each state file
			ListIterator<String> l = ctxFiles.listIterator ();
			while (l.hasNext ()) {
				
				String ctxFile = l.next ();
				
				if (LTSExtractor.tracesOn) {
					traceCounter++;
					out.write ("*** Trace " + traceCounter + " ***");
					out.newLine ();
					out.flush ();
				}

				// Creates stream to read from file
				FileReader r = new FileReader (ctxFile);
				BufferedReader in = new BufferedReader (r);

				currentState = m.getName ();
				String actionList = new String ();

				String str = new String ();
				while (str != null) {
					str = in.readLine ();

					if (str != null) {
						int i = str.indexOf (SEP);

						// State
						if (i == 0) {

							// Create state name
							String state = null;
							/*if (str.equals ("#END")) {
								state = END_STATE;
								ended = true;
							}
							else*/
								state = "Q" + str.substring (i+1);

							// If not in the model yet, inserts state
							if (!m.isInModel (state)) {
								m.insertState (state);
							}
							// NEW
							// If it is a repeated state, increases its occurrence counter
							else {
								State s = m.getState (state);
								s.incOccurrenceCounter ();
							}

							// Obtains the states by their names
							State dest = m.getState (state);
							State orig = m.getState (currentState);

							// If list of actions is empty, inserts the empty action
							if (actionList.length () == 0)
								actionList = EMPTY_ACTION;

							// Inserts new transition
							m.insertTransition (orig, dest, actionList);
							
							if (LTSExtractor.fullDebugOn) {
								System.out.println (currentState + " = " + actionList + " -> " + state);
							}
							
							// Sets control information
							currentState = state;
							actionList = new String ();
						}
						else {
							// Action

							String action = str;
							if (actionList.length () > 0)
								actionList += " -> " + action;
							else
								actionList += action;

							// Writes action in the trace file
							if (LTSExtractor.tracesOn) {
								out.write (action);
								out.newLine ();
								out.flush ();
							}
						}
					}
				}

				// Connects the last state in the trace to the final state
				State orig = m.getState (currentState);
				State dest = null;

				if (!ended || (actionList.length () > 0)) {
					if (!m.isInModel (FINAL_STATE))
						dest = m.insertState (FINAL_STATE);
					else
						dest = m.getState (FINAL_STATE);

					if (actionList.length () == 0)
						actionList = EMPTY_ACTION;

					m.insertTransition (orig, dest, actionList);
					
					if (LTSExtractor.fullDebugOn) {
						System.out.println (orig.getId () + " = " + actionList + " -> " + dest.getId ());
					}

					m.insertTransition (dest, dest, FINAL_ACTION);
					
					if (LTSExtractor.fullDebugOn) {
						System.out.println (dest.getId () + " = " + FINAL_ACTION + " -> " + dest.getId ());
					}
				}

				if (LTSExtractor.tracesOn) {
					out.newLine ();
					out.flush ();
				}

				// Closes buffer
				in.close ();
			}

			// Deletes state files
			if (!LTSExtractor.debugOn && !LTSExtractor.fullDebugOn) {
				String curDir = System.getProperty ("user.dir");
				if (OSUtils.isWindows())
					(new FileController ()).deleteFiles (curDir + "\\", ".ctx");
				else
					//if (OSUtils.isMac())
						(new FileController ()).deleteFiles (curDir + "//", ".ctx");
					//else {
						//System.out.println("Unsupported OS");
			    	//System.exit(1);
			    //}
			}

		}
		catch (Exception e) {
			throw new FSPGenerationException (e.getMessage ());
		}

		return m;
	}

	public void showTempFSP () {
		StateList sl = m.getStateList ();
		ListIterator<State> si = sl.getList ();

		System.out.println ("");
		System.out.println ("******************************************");

		while (si.hasNext ()) {
			State s = si.next ();

			System.out.print (s.getId () + " = " + "(");

			boolean first = true;

			ListIterator<Transition> ti = s.getTransitionList ();

			while (ti.hasNext ()) {
				Transition t = ti.next ();
				State s2 = t.getState ();
				String a2 = t.getActions ();

				if (first) {
					System.out.print (a2 + " -> " + s2.getId ());
					first = false;
				}
				else {
					System.out.println ("");
					System.out.print ("      |" + a2 + " -> " + s2.getId ());
				}
			}

			if (!si.hasNext ())
				System.out.println (").");
			else
				System.out.println ("),");

		}

		System.out.println ("");
		System.out.println ("******************************************");
	}
	
	/**
	 * Writes specification to the output file.
	 */
	private void writeSpec () throws FSPGenerationException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".writeSpec ()\n");
		}
		if (specFile != null) {
			String specLine = new String ();
			try {
				do {
					specLine = specFile.readLine ();
					if (LTSExtractor.fullDebugOn) {
						System.out.println("specLine = " + specLine);
					}
					if (specLine != null) {
						out.write (specLine);
						out.newLine ();
						out.flush ();
					}
				} while (specLine != null);
				out.newLine ();
				out.flush ();
			} catch (Exception e) {
				throw new FSPGenerationException ("Error writing specification to file!");
			}
		}
	}

	/**
	 * Generates an FSP description using the current model.
	 *
	 * @param fileName the name of the file to be generated.
	 */
	public void generateFSP (String modelName)
		throws FSPGenerationException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".generateFSP ("+modelName+")");
		}
	 	try {

			// Creates a stream to write to file
			FileWriter w = new FileWriter (modelName.trim () + "."+LTS_EXT);
			out = new BufferedWriter (w);
			
			// Writes the specification to the FSP File
			writeSpec ();

			StateList sl = m.getStateList ();
			ListIterator<State> si = sl.getList ();

			boolean hasNull = false;
			boolean init = true;

			while (si.hasNext ()) {
				State s = si.next ();
				ListIterator<Transition> ti = s.getTransitionList ();

				if (LTSExtractor.fullDebugOn) {
					System.out.println (s.getId ());
				}
				
				if (!init) {
					//if (!s.getId ().equals (END_STATE)) {
						out.write (",");
						out.newLine ();
						out.flush ();
					//}
				}
				else
					init = false;

				boolean first = true;

				if (!ti.hasNext ()) {
					//if (!s.getId ().equals (END_STATE))
						//if (!ended)
							out.write (s.getId () + " = " + FINAL_STATE);
						//else
							//out.write (s.getId () + " = " + END_STATE);
				}
				else {
					if (s.getId ().equals (m.getName ())) {
						
							if (LTSExtractor.debugOn || LTSExtractor.fullDebugOn) {
								out.write("deterministic ");
							}
							
							out.write (s.getId () + " = Q0");
					}
					else {
						out.write (s.getId () + " = ");

						while (ti.hasNext ()) {
							Transition sc = ti.next ();
							State s2 = sc.getState ();
							String id = s2.getId ();
							String a = sc.getActions ();

							if (a.equals (EMPTY_ACTION) && !hasNull)
								hasNull = true;

							if (first) {
								out.write ("(");
								first = false;
							}
							
							if (probabilistic) {
								BigDecimal stateCounter = new BigDecimal(s.getOccurrenceCounter ());
								BigDecimal transitionCounter = new BigDecimal(sc.getOccurrenceCounter ());
								BigDecimal prob = transitionCounter.divide (stateCounter, 4, BigDecimal.ROUND_HALF_UP);
								
								if (LTSExtractor.fullDebugOn) {
									System.out.println ("state = " + stateCounter);
									System.out.println ("transition = " + transitionCounter);
									System.out.println ("prob = " + prob + "\n");
								}
								
								out.write ("("+ prob + ") (" + a + " -> " + id + ")");
							}
							else {
								out.write (a + " -> " + id);
							}

							if (ti.hasNext ())
								out.write ("\n\t|");
							else
								out.write (")");
						}
					}
				}
				
				if (LTSExtractor.fullDebugOn) {
					showTempFSP ();
				}

				if (!si.hasNext ()) {
					if (hasNull) {
						out.newLine ();
						out.write ("\\{"+EMPTY_ACTION+"}");
					}
					out.write (".");
				}
		 	}

			out.close ();
		}
		catch (Exception e) {
			throw new FSPGenerationException (e.getMessage ());
		}
	}

	/**
	 * Returns the built model.
	 *
	 * @return a reference to the model.
	 */
	public Model getModel () {
		return m;
	}
 }