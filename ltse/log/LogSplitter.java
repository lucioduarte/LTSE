package ltse.log;

/**
 * Class that implements the partition of log file into logs for each instance.
 *
 * @author Lucio Mauro Duarte
 * @version 17/07/2013
 */

import java.io.*;
import java.util.*;
//import java.util.Map.Entry;

import ltse.LTSExtractor;
import ltse.file.Definitions;

public class LogSplitter implements Definitions {
	/** Buffer used to read information from the input file */
	private BufferedReader in;
	/** Table of identified instances */
	private Hashtable<String, BufferedWriter> instancesTable;
	/** Counter of instances */
	private int counter;
	/** Indicates whether the execution of the instances terminated normally */

	public LogSplitter () {
		// Initialises counter and creates table of instances
		counter = 0;
		instancesTable = new Hashtable<String, BufferedWriter> ();
		in = null;
	}

	/**
	 * Splits a log file into multiple other files according to annotations
	 * referring to different instances.
	 *
	 * @param logFile the original log file.
	 *
	 * @return the number of files resulting from the splitting.
	 */
	public int split (String logFile) throws LogFileException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".split("+logFile+")");
		}
		
		int i = logFile.lastIndexOf (".");
		String name = logFile.substring (0, i);
		if (LTSExtractor.fullDebugOn) {
			System.out.println("Log file name = " + name);
		}
		
		try {
			// Creates buffer to read from file
			//System.out.println ("*** LogFile = " + logFile);
			FileReader r = new FileReader (logFile);
			in = new BufferedReader (r);

			// Includes entries in the output files
			String entry = new String ();
			while (entry != null) {
				entry = in.readLine ();

				if (entry != null) {
					// Discovers the instance's ID
					String id = getId (entry);
					
					if (LTSExtractor.fullDebugOn) {
						System.out.println ("===> Instance = " + name + "." + id);
					}
					
					BufferedWriter b = null;
					// Looks for ID in the table
					if (id != null) {
						if (instancesTable.containsKey (id)) {
							// Obtains the reference to the output buffer for this instance
							b = getLogFile (id);
						}
						else {
							// Creates buffer to write to file associated to this instance
							counter++;
							String logName = name + "_" + counter + "." + LOG_EXT;
							FileWriter w = new FileWriter (logName);
							LTSExtractor.tempLogs.add(logName);
							b = new BufferedWriter (w);
							// Includes new instance in the table
							instancesTable.put (id, b);
						}

						// Writes entry on the appropriate log file using output buffer
						b.write (entry);
						b.newLine ();
						b.flush ();
					}
				}
			}

			// Closes all buffers
			in.close ();
			closeOutputBuffers ();
		}
		catch (Exception e) {
			throw new LogFileException ("Error splitting log file " + logFile +": " + e.getMessage ());
		}

		return counter;
	}

	/**
	 * Retrieves the ID of the instance from an annotation.
	 *
	 * @param annotation the annotation for which the instance ID is required.
	 *
	 * @return the instance ID.
	 */
	private String getId (String annotation) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getID("+annotation+")");
		}
		String e = new String (annotation);
		String id = null;

		int i = e.indexOf (LABEL_SEP);
		if (i > -1) {
			String type = e.substring (0, i);
			// Determines the type of the annotation
			if (type.equals (REP_ENTER) || type.equals (SEL_ENTER) ||
				  type.equals (REP_END) || type.equals (SEL_END) ||
			  	type.equals (CALL_ENTER) || type.equals (MET_ENTER) ||
			  	type.equals (CALL_END) || type.equals (MET_END) 
			  	|| type.equals(INT_CALL_ENTER) || type.equals(INT_CALL_END)) { //MODIFIED

				// Skips type
				e = e.substring (i+1);

				i = e.indexOf (SEP);
				// Skips control predicate/method name
				e = e.substring (i+1);
				i = e.indexOf (SEP);
				if (type.equals (REP_ENTER) || type.equals (SEL_ENTER)) {
					// Skips value of control predicate
					e = e.substring (i+1);
					i = e.indexOf (SEP);
				}
				e = e.substring (0, i);
				// Skips class name
				i = e.indexOf (CLASS_SEP);
				id = e.substring (i+1);
				
				//id = e.substring (0, i);
			}

			if (type.equals (ACTION)) {
				// Skips action name
				e = e.substring (i+1);
				i = e.indexOf (SEP);
				e = e.substring (i+1);
				// Skips class name
				i = e.indexOf (CLASS_SEP);
				id = e.substring(i+1);
			}
		}

		return id;
	}

	/**
	 * Returns a reference to log file used for a specific instance.
	 *
	 * @param id the instance ID.
	 *
	 * @return a reference to a buffer which will be used to write on the file.
	 */
	private BufferedWriter getLogFile (String id) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getLogFile("+id+")");
		}
		BufferedWriter b = instancesTable.get (id);
		return b;
	}

	/**
	 * Closes all buffers used to write on log files.
	 */
	private void closeOutputBuffers () throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".closeOutputBuffers()");
		}
		Enumeration<BufferedWriter> n = instancesTable.elements ();
		while (n.hasMoreElements ()) {
			BufferedWriter b = n.nextElement ();
			b.close () ;
		}
	}
}