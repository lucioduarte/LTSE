package ltse.model;

/**
 * Class that stores and manages information about a model.
 *
 * @author Lucio Mauro Duarte
 */

 

import java.util.*;

import ltse.file.Definitions;

import java.io.*;

public class Model implements Definitions {

	private String name;
	private StateList stateList;

	public Model (String n) {
		name = n;
		stateList = new StateList ();
	}

	/**
	 * Returns the name of the model.
	 *
	 * @return the model name.
	 */
	public String getName () {
		return name;
	}

	/**
	 * Returns the list of states of this model.
	 *
	 * @return the model state list.
	 */
	public StateList getStateList () {
		return stateList;
	}

	/************************* STATE OPERATIONS *************************/

	/**
	 * Inserts a new state in the list.
	 *
	 * @param id the ID of the new state.
	 *
	 * @return a reference to the newly inserted state.
	 */
	public State insertState (String id) {
		State s = new State (id);
		stateList.insert (s);

		return s;
	}

	/**
	 * Deletes a state from the list.
	 *
	 * @param id the ID of the state to be deleted.
	 */
	public void deleteState (String id) {
		//System.out.println (id);
		State s = stateList.get (id);
		stateList.remove (s);
	}

	/**
	 * Returns a reference to a state based on its ID.
	 *
	 * @param id the ID of the state.
	 *
	 * @return a reference to the state.
	 */
	public State getState (String id) {
		State s = stateList.get (id);
		return s;
	}

	/**
	 * Checks whether a certain state state is in the model.
	 *
	 * @param id the ID of the state.
	 *
	 * @return <code>true<\code> if the state exists and <code>false<\code>, otherwise.
	 */
	public boolean isInModel (String id) {
		State s = stateList.get (id);
		if (s != null)
			return true;
		return false;
	}

	/************************* TRANSITION OPERATIONS *************************/

	/**
	 * Inserts a new transition.
	 *
	 * @param s1 the origin of the transition.
	 * @param s2 the destination of the transition.
	 * @param a the label of the transition.
	 */
	public void insertTransition (State s1, State s2, String a) {
		s1.addTransition (s2, a);
	}

	/**
	 * Deletes a transition.
	 *
	 * @param s1 the origin of the transition.
	 * @param s2 the destination of the transition.
	 * @param a the label of the transition.
	 */
	public void deleteTransition (State s1, State s2, String a) {
		s1.removeTransition (s2, a);
	}

	/**
	 * Checks whether there is a transition between two given states which is labelled
	 * with a certain sequence of actions.
	 *
	 * @param s1 the origin of the transition.
	 * @param s2 the destination of the transition.
	 * @param a the label of the transition.
	 *
	 * @return <code>true<\code> if the transition exists and <code>false<\code>, otherwise.
	 */
	public boolean transitionExists (State s1, State s2, String a) {
		return s1.isInList (s2, a);
	}

	/**
	 * Checks whether a given state has any transition.
	 *
	 * @param s the origin of the transition.
	 *
	 * @return <code>true<\code> if a transition with this origin exists and <code>false<\code>, otherwise.
	 */
	/*public boolean stateHasTransition (State s) {
		return s.hasTransitions ();
	}*/


	/************************* FILE OPERATIONS ******************************/

	/**
	 * Saves the contents of the current model in a file.
	 *
	 * @param name the name of the file where the model is to be saved in
	 */
	public void saveModel (String name) throws ModelException {
		System.out.println ("Saving model...");
		try {
			FileWriter f = new FileWriter (name.trim () + "."+MOD_EXT);
			BufferedWriter out = new BufferedWriter (f);

			ListIterator<State> si = stateList.getList ();
			while (si.hasNext ()) {
				State s = si.next ();
				out.write (s.getId () + SEP + s.getOccurrenceCounter ());
				out.newLine ();

				ListIterator<Transition> ti = s.getTransitionList ();
				while (ti.hasNext ()) {
					Transition t = ti.next ();
					State s2 = t.getState ();
					String a = t.getActions ();
					out.write (s2.getId () + SEP + a + SEP + t.getOccurrenceCounter ());
					out.newLine ();
				}

				out.write (SEP);
				out.newLine ();
				out.flush ();
			}

			out.close ();
			System.out.println ("The model has been saved as " + name + "."+MOD_EXT+"\n");
		}
		catch (Exception e) {
			throw new ModelException ("Error saving model: " + e.getMessage ());
		}
	}

	/**
	 * Loads the contents from a model saved in a file.
	 *
	 * @param fileName the name of the file where the model is to be loaded from
	 */
	public void loadModel (String fileName) throws ModelException {
		System.out.println ("Loading model...");
		try {
			FileReader f = new FileReader (fileName);
			BufferedReader in = new BufferedReader (f);

			boolean first = true;

			String entry = new String ();
			while (entry != null) {
				entry = in.readLine ();

				if (entry != null) {
					// Inserts state
					State s1 = null;
					if (!isInModel (entry))
						s1 = insertState (entry);
					else
						s1 = getState (entry);

					if (first) {
						name = entry;
						first = false;
					}

					while (!entry.equals (SEP)) {
						entry = in.readLine ();
						int i = entry.indexOf (SEP);
						if (i > 0) {
							String did = entry.substring (0, i);
							State s2 = null;
							if (!isInModel (did))
					 			s2 = insertState (did);
							else
								s2 = getState (did);
							String a = entry.substring (i+1);

							// Inserts transition
							insertTransition (s1, s2, a);
						}
					}
				}
			}

			in.close ();
			System.out.println ("The model has been loaded from " + fileName + "\n");
		}
		catch (Exception e) {
			throw new ModelException ("Error loading model: " + e.getMessage ());
		}
	}

	/**
	 * Returns the approximate size of the model in memory.
	 *
	 * @return the approximate size in bytes of the model in memory.
	 */
	public int size () {
		int modelSize = 0;
		if (stateList != null)
			modelSize = (2 * name.length ()) + stateList.size ();
		return modelSize;
	}

	/*public static void main (String args []) {
		Model m = new Model ("test");
		try {
			m.loadModel ("T1.mdl");
			m.saveModel ("New");
		}
		catch (ModelException e) {
			System.out.println (e.getMessage());;
		}
	}*/
}