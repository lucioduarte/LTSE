package ltse.fsp;

/**
 * Exception thrown to signal a problem during the FSP generation process.
 *
 * @author Lucio Mauro Duarte
 */

 

public class FSPGenerationException extends Exception {
 	/**
   * 
   */
  private static final long serialVersionUID = 3367378621301722008L;
	private String msg;

	public FSPGenerationException (String m) {
		super ();
		msg = m;
	}

	@Override
	public String getMessage () {
		return "*** Error creating FSP specification: " + msg;
	}
}