package ltse.model;

/**
 * Class that implements a state of an LTS model.
 *
 * @author Lucio Mauro Duarte
 */

 

import java.util.*;

public class State {
	private String id;
	private TransitionList transitionList;
	private int occurrenceCounter; // NEW

	public State (String i) {
		id = i;
		transitionList = new TransitionList ();
		occurrenceCounter = 1; // NEW
	}
	
	public int getOccurrenceCounter () {
		return occurrenceCounter;
	}
	
	public void incOccurrenceCounter () {
		occurrenceCounter++;
	}

	/**
	 * Returns the ID of this state.
	 *
	 * @return the state ID.
	 */
	public String getId () {
		return id;
	}

	/**
	 * Returns the list of states of this model.
	 *
	 * @return the model state list.
	 */
	public ListIterator<Transition> getTransitionList () {
		return transitionList.getList ();
	}

	/**
	 * Inserts a new transition.
	 *
	 * @param s2 the destination of the transition.
	 * @param a the label of the transition.
	 */
	public void addTransition (State s2, String a) {
		transitionList.addTransition (s2, a);
	}

	/**
	 * Deletes a transition.
	 *
	 * @param s1 the origin of the transition.
	 * @param s2 the destination of the transition.
	 * @param a the label of the transition.
	 */
	public void removeTransition (State s2, String a) {
		transitionList.removeTransition (s2, a);
	}

	/**
	 * Checks whether there is a transition between two given states which is labelled
	 * with a certain sequence of actions.
	 *
	 * @param s2 the destination of the transition.
	 * @param a the label of the transition.
	 *
	 * @return <code>true<\code> if the transition exists and <code>false<\code>, otherwise.
	 */
	public boolean isInList (State s2, String a) {
		return transitionList.isInList (s2, a);
	}

	/**
	 * Checks whether this state has any transition.
	 * @return <code>true<\code> if a transition with this origin exists and <code>false<\code>, otherwise.
	 */
	/*public boolean hasTransitions () {
		if (transitionList.isEmpty ())
			return false;

		return true;
	}*/

	/*public boolean hasOnlyEmptyTransitions () {
		ListIterator i = transitionList.getList ();
		while (i.hasNext ()) {
			Transition t = (Transition) i.next ();
			String a = t.getActions ();

			if (!a.equals ("null"))
				return true;
		}

		return false;
	}*/

	/*public boolean hasMoreThanOneTransition () {
		ListIterator l = transitionList.getList ();

		if (l.hasNext ()) {
			Transition t = (Transition) l.next ();

			if (l.hasNext ())
				return true;
		}

		return false;
	}*/

	/**
	 * Returns the approximate size of the state in memory.
	 *
	 * @return the approximate size in bytes of the state in memory.
	 */
	public int size () {
		int stateSize = (2 * id.length ()) + transitionList.size ();
		//System.out.println ("State size = " + stateSize);
		return stateSize;
	}
}