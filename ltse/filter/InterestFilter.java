package ltse.filter;

/**
 * Class used to filter events and select the ones which are of interest.
 *
 * @author Lucio Mauro Duarte
 */

 

import java.util.LinkedList;
import java.util.ListIterator;

import ltse.LTSExtractor;

import java.io.FileReader;
import java.io.BufferedReader;



public class InterestFilter {

	/** List of methods of interest */
	private LinkedList<String> actionList;
	/** Flag to indicate whether a filter of methods is used */
	private boolean hasActions;

	public InterestFilter () {
		actionList = new LinkedList<String> ();
		hasActions = false;
	}

	/**
	 * Creates a new filter of interests based on information contained in a
	 * filter file.
	 *
	 * @param filterFile the name of the filter file
	 */
	public void createFilter (String filterFile) throws FilterException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".createFilter ("+filterFile+")");
		}
		
		FileReader f = null;
		BufferedReader in = null;
		try {
			f = new FileReader (filterFile);
			in = new BufferedReader (f);

			String aux = null;

			// Handles the list of actions, if it exists in the file
			if (LTSExtractor.fullDebugOn) {
				System.out.print ("--> Alphabet = { ");
			}
			do {
				aux = in.readLine ();

				if (aux != null) {
					if (!hasActions)
						hasActions = true;
					// Adds action name to the list
					actionList.add (aux.trim ());
					
					if (LTSExtractor.fullDebugOn) {
						System.out.print (aux.trim ()+" ");
					}
				}
			} while (aux != null);
			
			if (LTSExtractor.fullDebugOn) {
				System.out.println ("}\n");
			}
			
			in.close();
		}
		catch (Exception e) {
			throw new FilterException (e.getMessage ());
		}
	}

	/**
	 * Returns the value of the flag that indicates the use of a filter of actions
	 *
	 * @return the value of the flag that indicates the use of a filter of actions
	 */
	public boolean hasActionsOfInterest () {
		return hasActions;
	}

	/**
	 * Checks whether an action is in the list of actions of interest
	 *
	 * @param a the action to look for in the list
	 *
	 * @return <code>true<\code> if the action is of interest and <code>false<\code>
	 *         otherwise
	 */
	public boolean isActionOfInterest (String a) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".isActionOfInterest ("+a+")");
		}
		
		boolean found = false;
		ListIterator<String> i = actionList.listIterator ();
		
		if (LTSExtractor.fullDebugOn) {
			System.out.println ("a = " + a.trim ());
		}
		
		while (i.hasNext () && !found) {
			String action = new String (i.next ());
			
			if (LTSExtractor.fullDebugOn) {
				System.out.println ("action = " + action.trim ());
			}
			
			if (action.equals (a.trim ())) {
				
				if (LTSExtractor.fullDebugOn) {
					System.out.println ("ADDED = " + a);
				}
				
				found = true;
			}
		}

		return found;
	}
	
	/*public static void main (String args[]) {
		InterestFilter filter = new InterestFilter ();
		
		filter.actionList.add ("teste");
		filter.actionList.add ("teste2");
		filter.actionList.add ("teste3");
		filter.actionList.add ("teste4");
		
		if (!filter.isActionOfInterest ("teste"))
			System.out.println ("ERROR!");
		if (!filter.isActionOfInterest ("teste2"))
			System.out.println ("ERROR2!");
		if (!filter.isActionOfInterest ("teste3"))
			System.out.println ("ERROR3!");
		if (!filter.isActionOfInterest ("teste4"))
			System.out.println ("ERROR4!");
		if (filter.isActionOfInterest ("teste5"))
			System.out.println ("ERROR5!");
	}*/
}