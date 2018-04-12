package ltse.model;

/**
 * Class that represents a transition between two states of a model.
 *
 * @author Lucio Mauro Duarte
 */

 

public class Transition {
	private State dest;
	private String actions;
	private int occurrenceCounter; // NEW

	public Transition (State d, String l) {
		dest = d;
		actions = l;
		occurrenceCounter = 1; // NEW
	}
	
	public void incOccurrenceCounter () { // NEW
		occurrenceCounter++;
	}

	public State getState () { return dest;	}
	public String getActions () { return actions; }
	public int getOccurrenceCounter () { return occurrenceCounter; } // NEW

	/**
	 * Returns the approximate size of the transition in memory.
	 *
	 * @return the approximate size in bytes of the transition in memory.
	 */
	public int size () {
		int transitionSize = (2 * actions.length ()) + 4;
		//System.out.println ("Transition size = " + transitionSize);
		return transitionSize;
	}
}