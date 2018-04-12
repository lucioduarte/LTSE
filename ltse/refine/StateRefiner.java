package ltse.refine;

/**
 * Class used to refine states.
 *
 * @author Lucio Mauro Duarte
 */

 

import java.util.LinkedList;
import java.util.ListIterator;
import java.io.FileReader;
import java.io.BufferedReader;

public class StateRefiner {

	/** List of attributes to be used in the refinement */
	private LinkedList<String> refineList;
	/** Flag to indicate whether a refinement is necessary */
	private boolean hasRefinement;

	public StateRefiner () {
		refineList = new LinkedList<String> ();
		hasRefinement = false;
	}

	/**
	 * Creates a state refiner based on information contained in a refinement file.
	 *
	 * @param refineFile the name of the refinement file.
	 */
	public void createStateRefiner (String refineFile) throws RefinementException {

		try {
			FileReader f = new FileReader (refineFile);
			BufferedReader in = new BufferedReader (f);

			String aux = new String ();

			do {
				aux = in.readLine ();

				if (aux != null) {
					if (!hasRefinement)
						hasRefinement = true;
					System.out.println ("\n ==> Attribute for refinement: " + aux);
					refineList.add (aux);
				}
			} while (aux != null);
			
			in.close();
		}
		catch (Exception e) {
			throw new RefinementException (e.getMessage ());
		}
	}

	/**
	 * Returns the value of the flag that indicates the use of refinements
	 *
	 * @return the value of the flag that indicates the use of refinements
	 */
	public boolean hasStateRefinement () {
		return hasRefinement;
	}

	/**
	 * Checks whether an attribute is in the list of refinements
	 *
	 * @param attrib the attribute to look for in the list
	 *
	 * @return <code>true<\code> if the attribute is in the list and <code>false<\code>
	 *         otherwise
	 */
	public boolean isRefinement (String a) {
		boolean found = false;
		ListIterator<String> i = refineList.listIterator ();
		while (i.hasNext () && !found) {
			String attrib = new String (i.next ());
			if (attrib.trim().equals (a.trim ())) {
				found = true;
			}
		}

		return found;
	}
}