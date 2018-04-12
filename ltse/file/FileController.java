package ltse.file;

/**
 * Class that implements all functions related to files and directories
 * maintenance.
 *
 * @author Lucio Mauro Duarte
 * @version 20/11/2010 
 */

import java.io.File;
import java.io.FilenameFilter;

public class FileController {
	/**
	 * Deletes all files with the same extension in a directory.
	 *
	 * @param d the directory where the files are.
	 * @param e the extension of the files.
	 */
	public void deleteFiles (String d, String e) {
		ExtensionFilter filter = new ExtensionFilter (e);
		File dir = new File (d);

    String[] list = dir.list (filter);
    if (list.length == 0) return;

    for (int i = 0; i < list.length; i++) {
      File file = new File(d + list[i]);
      deleteFile (file.getName ());
    }
  }

	/**
	 * Deletes a specific file.
	 *
	 * @param f the file name.
	 */
  public void deleteFile (String f) {
  	//System.out.println ("deleting " + f);
  	(new File (f)).delete ();
  }

  /*
   * Creates a new directory.
   *
   * @param d The name of the new directory.
   */
  public void createDir (String d) {
  	(new File (d)).mkdir ();
  }

	/*
	 * Identifies whether a file has a certain extension.
	 */
	class ExtensionFilter implements FilenameFilter {
		/* Stores the extension to be used as filter */
  	private String extension;

  	/*
  	 * Creates a new extension filter.
  	 * 
  	 * @param filter the extension to be used as filter
  	 */
    public ExtensionFilter (String extension) {
    	this.extension = extension;
    }

    /*
     * (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    @Override
		public boolean accept (File dir, String name) {
    	return (name.endsWith (extension));
    }
  }
}