package ltse.filter;

/**
 * Exception thrown to signal a problem during the filtering process.
 *
 * @author Lucio Mauro Duarte
 */

 

public class FilterException extends Exception {
	/**
   * 
   */
  private static final long serialVersionUID = 3228336370051964436L;
	private String msg;

	public FilterException (String m) {
		super ();
		msg = m;
	}

	@Override
	public String getMessage () {
		return "*** Error creating filter of interests: " + msg;
	}
}