package ltse.log;

/**
 * Exception thrown to signal a problem using/accessing the log file.
 *
 * @author Lucio Mauro Duarte
 */

 

public class LogFileException extends Exception {
	/**
   * 
   */
  private static final long serialVersionUID = 1L;
	private String msg;

	public LogFileException (String m) {
		super ();
		msg = m;
	}

	@Override
	public String getMessage () {
		return "*** Error creating context annotations: " + msg;
	}
}