package ltse.splitter;

/**
 * Exception thrown to signal a problem splitting a log file containing annotations of
 * more than one class.
 *
 * @author Lucio Mauro Duarte
 * @version 20/11/2010
 */

public class ClassSplittingException extends Exception {

	/* Serial UID */
	private static final long	serialVersionUID	= -7659800613492970415L;
	/* Exception message */
	private String msg;

	/*
	 * Creates a new instance of this exception
	 */
	public ClassSplittingException (String m) {
		super ();
		msg = m;
	}

	/*
	 * Constructs the exception message
	 */
	@Override
	public String getMessage () {
		return "*** Error splitting log file into classes: " + msg;
	}
}
