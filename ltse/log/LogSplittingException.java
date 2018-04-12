package ltse.log;

/**
 * Exception thrown to signal a problem splitting a log file.
 *
 * @author Lucio Mauro Duarte
 * @version 20/11/2010
 */

public class LogSplittingException extends Exception {

	/* Serial UID */ 
  private static final long serialVersionUID = 1L;
  /* Exception message */
	private String msg;

	/*
	 * Creates a new instance of this exception
	 */
	public LogSplittingException (String m) {
		super ();
		msg = m;
	}

	/*
	 * Constructs the exception message
	 */
	@Override
	public String getMessage () {
		return "*** Error splitting log file: " + msg;
	}
}