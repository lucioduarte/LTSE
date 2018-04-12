package ltse.model;

/**
 * Class that implements a list of states of a model.
 *
 * @author Lucio Mauro Duarte
 */

 

import java.util.*;

public class StateList {
	private Vector<State> states;

	public StateList () {
		states = new Vector<State> ();
	}

	/**
	 * Inserts a new state in the list.
	 *
	 * @param n the state to be inserted.
	 */
	public void insert (State n) {
		states.addElement (n);
	}

	/**
	 * Removes a state from the list.
	 *
	 * @param n the state to be removed.
	 */
	public boolean remove (State n) {
		return states.removeElement (n);
	}

	/**
	 * Retrieves a state from the list based on its ID.
	 *
	 * @param id the ID of the state
	 *
	 * @return the reference to the state
	 */
	public State get (String id) {
		Enumeration<State> e = states.elements ();
		boolean found = false;
		State n = null;
		while (e.hasMoreElements () && !found) {
			n = e.nextElement ();
			if (n.getId ().equals (id))
				found = true;
		}
		if (found)
			return n;
		else
			return null;
	}

	/**
	 * Returns the list of states.
	 *
	 * @return a reference to the list of states.
	 */
	public ListIterator<State> getList () {
		return states.listIterator ();
	}

	/**
	 * Returns the approximate size of the state list in memory.
	 *
	 * @return the approximate size in bytes of the state list in memory.
	 */
	public int size () {
		int listSize = 0;
		if (states.size () > 0) {
			Enumeration<State> e = states.elements ();
			State n = null;
			while (e.hasMoreElements ()) {
				n = e.nextElement ();
				listSize += n.size ();
			}
		}
		//System.out.println ("State list size = " + listSize);
		return listSize;
	}
}