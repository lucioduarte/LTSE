package ltse.context;

/**
 * Exception thrown to signal a problem during the process of saving/loading
 * a context table.
 *
 * @author Lucio Mauro Duarte
 */

 

public class ContextTableException extends Exception {
	/**
   * 
   */
  private static final long serialVersionUID = -4918305654704936627L;
	private String msg;

	public ContextTableException (String m) {
		super ();
		msg = m;
	}

	@Override
	public String getMessage () {
		return msg;
	}
}