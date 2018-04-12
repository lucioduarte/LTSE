package ltse.refine;

/**
 * Exception thrown to signal a problem during the refinement process.
 *
 * @author Lucio Mauro Duarte
 */

 

public class RefinementException extends Exception {
	/**
   * 
   */
  private static final long serialVersionUID = 7542961972363645741L;
	private String msg;

	public RefinementException (String m) {
		super ();
		msg = m;
	}

	@Override
	public String getMessage () {
		return "*** Error creating refinement: " + msg;
	}
}