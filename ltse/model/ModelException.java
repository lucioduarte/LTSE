package ltse.model;

/**
 * Exception thrown to signal a problem during the process of saving/loading
 * a model.
 *
 * @author Lucio Mauro Duarte
 */

 

public class ModelException extends Exception {
	/**
   * 
   */
  private static final long serialVersionUID = 3916527595033177493L;
	private String msg;

	public ModelException (String m) {
		super ();
		msg = m;
	}

	@Override
	public String getMessage () {
		return msg;
	}
}