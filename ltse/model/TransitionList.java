package ltse.model;

/**
 * Class that implements a list of transitions of a model.
 *
 * @author Lucio Mauro Duarte
 */

 

import java.util.*;

public class TransitionList {
	private Vector<Transition> transitions;

	public TransitionList () {
		transitions = new Vector<Transition> ();
	}

/**
	 * Adds a transition between this state and another one.
	 * Labels the trasition with a sequence of actions.
	 *
	 * @param s2 the destination of the transition.
	 * @param a the sequence of actions labelling this transition.
	 */
	public void addTransition (State s2, String a) {
		if (!isInList (s2, a)) {
			// If transition is new, creates a new transition and adds it to the list
			Transition t = new Transition (s2, a);
			transitions.addElement (t);
		}
		else { // NEW
			// If it is a repeated transition, increases its occurrence counter
			Transition t2 = getTransition (s2, a);
			t2.incOccurrenceCounter ();
		}
	}

	/**
	 * Removes a transition.
	 *
	 * @param s2 the destination of the transition to be removed.
	 * @param a the sequence of actions labelling the transition.
	 */
	public void removeTransition (State s2, String a) {
		if (isInList (s2, a)) {
			Transition t = getTransition (s2, a);
			transitions.removeElement (t);
		}
	}

	/**
	 * Checks whether a transition exists.
	 *
	 * @param s2 the destination of the transition.
	 * @param a the sequence of actions labelling the transition.
	 *
	 * @return <code>true<\code> if the transition exists and <code>false<\code>,
	 *         otherwise.
	 */
	protected boolean isInList (State s2, String a) {
		ListIterator<Transition> i = transitions.listIterator ();
		boolean found = false;
		String s2Id = s2.getId ();

		while (i.hasNext () && !found) {
			Transition t = i.next ();
			State dest = t.getState ();
			String did = dest.getId ();
			String ac = t.getActions ();

			if (did.equals (s2Id) && ac.equals (a))
				found = true;
		}

		return found;
	}

	/**
	 * Retrieves a reference to a transition.
	 *
	 * @param s2 the destination of the transition.
	 * @param a the sequence of actions labelling the transition.
	 *
	 * @return a reference to the transition, if it exists, and
	 *         <code>null<\code>, otherwise.
	 */
	private Transition getTransition (State s2, String a) {
		ListIterator<Transition> i = transitions.listIterator ();
		boolean found = false;
		Transition t = null;
		String s2Id = s2.getId ();

		while (i.hasNext () && !found) {
			t = i.next ();
			State dest = t.getState ();
			String ac = t.getActions ();
			String did = dest.getId ();

			if (did.equals (s2Id) && ac.equals (a))
				found = true;
		}

		return t;
	}

	/**
	 * Returns the list of transitions.
	 *
	 * @return a reference to the list of transitions.
	 */
	public ListIterator<Transition> getList () {
		return transitions.listIterator ();
	}

	/**
	 * Checks whether this state has transitions.
	 *
	 * @return <code>true<\code> if the state has no transitions
	 *         and <code>false<\code>, otherwise.
	 */
	public boolean isEmpty () {
		ListIterator<Transition> i = transitions.listIterator ();
		boolean empty = true;

		if (i.hasNext ())
			empty = false;

		return empty;
	}

	public int tam () {
		return transitions.size ();
	}

	/**
	 * Returns the approximate size of the transition list in memory.
	 *
	 * @return the approximate size in bytes of the transition list in memory.
	 */
	public int size () {
		int listSize = 0;
		if (transitions.size () > 0) {
			ListIterator<Transition> i = transitions.listIterator ();
			while (i.hasNext ()) {
				Transition t = i.next ();
				//System.out.println ("Transition " + t);
				listSize += t.size ();
			}
		}
		//System.out.println ("Transition list size = " + listSize);
		return listSize;
	}
}