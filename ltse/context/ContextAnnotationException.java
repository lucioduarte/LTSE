package ltse.context;

/**
 * Exception thrown to signal a problem during the context annotation process.
 *
 * @author Lucio Mauro Duarte
 */

 

public class ContextAnnotationException extends Exception {
	/**
   * 
   */
  private static final long serialVersionUID = 4935752517471023782L;
	private String msg;

	public ContextAnnotationException (String m) {
		super ();
		msg = m;
	}

	@Override
	public String getMessage () {
		return "*** Error creating context annotations: " + msg;
	}
}