package ltse;

/**
 * Class that implements the LTS extractor.
 *
 * @author Lucio Mauro Duarte
 */

 
import ltse.filter.*;
import ltse.refine.*;
import ltse.splitter.ClassSplitter;
import ltse.context.*;
import ltse.file.Definitions;
import ltse.file.FileController;
import ltse.file.OSUtils;
import ltse.fsp.*;
import ltse.log.LogFileException;
import ltse.log.LogSplitter;
import ltse.model.*;

import java.util.LinkedList;
import java.util.Hashtable;
import java.util.ListIterator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Extracts an FSP description from an instrumented code
 */
public class LTSExtractor implements Definitions {

	/** Filter used to select actions to appear in the model */
  private static InterestFilter filter = null;
  /** Set of attributes used to refine the model */
  private static StateRefiner refiner = null;
  /** Temporary logs */
  public static LinkedList<String> tempLogs = null;
  /** Defines whether debug mode is on */
  public static boolean debugOn = false;
  /** Defines whether full debug mode is on */
  public static boolean fullDebugOn = false;
  /** Defines whether trace output mode is on */
  public static boolean tracesOn = false;
  /** Counter used to control the number of arguments read from the command line */
  private static int counter = 0;
  /** Controls the total number of arguments provided via command line */
  private static int argNumber = 0;
  /** Determines model mode according what actions represent
   * Mode CALL: actions describe method calls
   * Mode TERMINATION: actions describe method termination
   * Mode ENTRY_AND_EXIT: there are actions for method call and termination */
  private static int mode = CALL;  
  /** Determines the type of model to be built: normal (false) or probabilistic (true) */
  private static boolean probabilistic = false;
  /** Input stream to a specification file (null if not provided) */
  private static BufferedReader specFile = null;

  /**
   * Checks which of the available options have been selected
   * 
   * @param args the list of parameters provided in the command line
   */
  private static void checkOptions (String args[]) {
    // Checks whether help was required
    String h = args[counter];
    if (h.equals ("-help"))
    	displayUsage ();

    // Checks whether the debug mode is on
    String d = args[counter];
    if (d.equals ("-debug")) {
      debugOn = true;
      System.out.println("\n==> Debug mode ON\n");
      counter++;
    }
    else
    	if (d.equals ("-fdebug")) {
    		fullDebugOn = true;
    		System.out.println("\n==> Full debug mode ON\n");
    		counter++;
    	}

    // Checks whether traces output is on
    String tr = args[counter];
    if (tr.equals ("-traces")) {
      tracesOn = true;
      System.out.println("\n==> Trace mode ON\n");
      counter++;
      // Because no order is predetermined, checks again whether the
      // option of debug mode is provided
      d = args[counter];
      if (d.equals ("-debug")) {
        debugOn = true;
        System.out.println("\n==> Debug mode ON\n");
        counter++;
      }
      else
      	if (d.equals ("-fdebug")) {
      		fullDebugOn = true;
      		System.out.println("\n==> Full debug mode ON\n");
      		counter++;
      	}
    }
  }

  /**
   * Creates an alphabet of actions according to actions contained in a filter file
   * 
   * @param args the list of parameters provided in the command line
   */
  private static void createFilter (String args[]) throws FilterException {
    // Creates a filter to select attributes and methods of
    // interest
    filter = new InterestFilter ();
    String filterFile = args[counter];
      
    // If a filter file is provided, creates an alphabet based on the names 
    // of actions in it
    int i = filterFile.lastIndexOf (".");
    String extension = filterFile.substring (i+1);
    if (extension.equals (FLT_EXT)) {
      filter.createFilter (filterFile);
      System.out.println ("\n==> Identification of alphabet complete!\n");
      counter++;
    }
  }

  /**
   * Creates a list of attributes to refine model states
   * 
   * @param args the list of parameters provided in the command line
   */
  private static void createRefinement (String args[]) throws RefinementException {
    // Creates a list of attributes to be used to refine states
    refiner = new StateRefiner ();
    String refineFile = args[counter];
      
    // If a refinement file name is provided, uses the list of attributes 
    // in it to create a refinement list
    int i = refineFile.lastIndexOf (".");
    String extension = refineFile.substring (i+1);
    if (extension.equals (REF_EXT)) {
    	System.out.println ("\n ==> Refinement file " + refineFile + " found!");
      refiner.createStateRefiner (refineFile);
      System.out.println ("\n ==> Identification of refinements complete!\n");
      counter++;
    }
  }

  /**
   * Determines how actions should interpreted during model construction
   * 
   * @param args the list of parameters provided in the command line
   */
  private static void actionMode (String args[]) {
    // Chooses the mode for passive or active processes
    String option = args[counter];
       
    // Selects the call mode, where actions refer to method calls 
    if (option.equals ("-c")) {
      mode = CALL;
      System.out.println("==> Mode Set = CALL");
      counter++;
    }
    else {
    	// Selects the termination mode, where actions refer to method termination
      if (option.equals ("-t")) {
      	mode = TERMINATION;
      	System.out.println("==> Mode Set = TERMINATION");
        counter++;
      }
      else {
      	// Selects the entry-and-exit mode, where actions refer to either entering or 
      	// exiting a method execution
        if (option.equals ("-e")) {
        	mode = ENTRY_AND_EXIT;
        	System.out.println("==> Mode Set = ENTRY_AND_EXIT");
          counter++;
        }
       	// If no option is selected, the default mode is the call mode
        else  {
        		mode = CALL;
        		System.out.println("==> Mode Set = CALL");
        }
      }
    }
   }
  
  /**
   * Checks the type of the model to be built and sets the appropriate flag.
   * 
   * @param args the list of arguments provided by the user.
   */
  private static void modelType (String args[]) {
  	String type = args[counter];
  	
  	if (type.equals ("-prob")) {
  		probabilistic = true;
  		counter++;
  	}
  }
    
  /**
   * Obtains the list of log files provided by command line.
   * 
   * @param args the list of parameters provided in the command line
   * @return a list of names of log files, separated by instances
   */
  private static Hashtable<String, LinkedList<String>> getLogList (String args[]) throws LogFileException {
  	if (LTSExtractor.fullDebugOn) {
			System.out.println("ltse.LTSExtractor.getLogList("+args+")");
		}
  	// Creates a table to store identified classes and the corresponding log files
    Hashtable<String, LinkedList<String>> filesByClass = new Hashtable<String, LinkedList<String>> ();
    
    // Collects the list of the names of the original log files
    System.out.println("\nCollecting log names...");
    LinkedList<String> originalLogs = getLogFiles (args);
    System.out.println("\n ==> All logs collected!\n");
    
    if (!originalLogs.isEmpty()) {
    	
    	// Creates a list to store the names of temporary log files
      tempLogs = new LinkedList<String> ();
    	
  		// Identifies and separates traces of different classes into different log files
      ClassSplitter c = new ClassSplitter ();
      System.out.println("\nSeparating classes...");
      Hashtable<String, Enumeration<String>> classLogs = c.split (originalLogs);
      System.out.println("\n ==> Classes splitted!\n");
      
      Enumeration<String> ll = classLogs.keys(); 
      while (ll.hasMoreElements()) {
      	String className = ll.nextElement();
      	
      	if (fullDebugOn) {
      		System.out.println("Class Name = " + className);
      	}
      	
      	LinkedList<String> logs = new LinkedList<String> ();
      	
      	Enumeration<String> logNames = classLogs.get(className);
      	
      	// Identifies and separate traces of different instances
				// of the same class into different log files
      	while (logNames.hasMoreElements() ) {
      		String logName = logNames.nextElement();
      		tempLogs.add (logName);
      		
      		if (fullDebugOn) {
        		System.out.println("Log Name = " + logName);
        	}
      		
      		LogSplitter l = new LogSplitter ();
      		int n = l.split (logName);
      		//System.out.println ("n = " + n);
      		// If more than an instance identified, adds one log file for each
      		// different instance to the logs list
      		int i = logName.lastIndexOf (".");
      		//System.out.println ("i = " + i);
    			logName = logName.substring (0, i);
      		while (n > 0) {
      			// Adds file name to the list
      			String instanceLog = logName + "_" + n + "." + LOG_EXT;
       			logs.add(instanceLog);
      			tempLogs.add (instanceLog);
      			n--;
      		}
      	}
      	
      	filesByClass.put(className, logs);
			}
    }
      
    return filesByClass;
  }
  
  /**
   * Obtains the list of log files provided by command line
   * 
   * @param args the list of parameters provided in the command line
   * @return a list of names of log files
   */
  private static LinkedList<String> getLogFiles (String args[]) throws LogFileException {
  	if (LTSExtractor.fullDebugOn) {
			System.out.println("ltse.LTSExtractor.getLogFile("+args+")");
		}
    // Controls whether a log file has been provided
    boolean logFound = false;
    // Controls whether all log files have been processed
    boolean noMoreLogs = false;
    // List of the names of the original log files
    LinkedList<String> logs = new LinkedList<String> ();
   
    // Collects all log files
    while (!noMoreLogs) {
    	// Checks whether there is another parameter to be processed
    	if (counter < args.length) {
    		// Obtains name of the log file
    		String fileName = args[counter];
    		
    		if (fullDebugOn) { 
    			System.out.println ("fileName = " + fileName);
    		}
    		
    		int i = fileName.lastIndexOf (".");
        
    		// Checks whether the file contains an extension
    		if (i > 0) {
    			// Obtains the file extension
    			String ext = fileName.substring (i+1);
    			
    			if (fullDebugOn) {
    				System.out.println ("ext = " + ext);
    			}
    			
    			// If extension is different from the one expected
    			// stops the process
    			if (!ext.equals (LOG_EXT)) {
    				
    				if (fullDebugOn) {
    					System.out.println("***** End of list of logs *****");
    				}
    				
    				noMoreLogs = true;
    				if (!logFound)
    					throw new LogFileException ("Unexpected log file extension");
    			}
    			else {
    				// If the extension is correct, log found
    				// Flags that a log file has been found
    				logFound = true;	
    				// Adds the log file name to the list
    				logs.add(fileName);
    				
    				if (fullDebugOn) {
							System.out.println ("Added to log list = " + fileName);
						}
    				
    				// Advances to the next parameter
    				counter++;
    			}
    		}
    		else {
    			// If the parameter has no extension,
    			// checks whether a log file has been previously found
    			if (logFound) {
    				// If a log file has been found, sets the flag to
    				// exit the loop successfully
    				noMoreLogs = true;
    			}
    			else {
    				// If no log has been found, raises an exception
    				throw new LogFileException ("Unexpected log file extension");
    			}
    		}
    	}
    	else
    		// If there are no more arguments to process, 
    		// sets the flag to exit the loop
    		noMoreLogs = true;
    }
    
    return logs;
  }

  /**
   * Loads information from a context table saved in a file provided as parameter
   * 
   * @param args the list of parameters provided in the command line
   * @param name the name of the class which this context table belongs to
   * 
   * @return a context table built based on the information from a context table file
   */
  private static ContextTable loadContextTable (String args[], String name) throws ContextTableException {
  	// Loads the necessary information to reconstruct a context table
    System.out.println("Looking for CT file...");
    ContextTable t = null;
    if ((counter + 1) < argNumber) {
      String ctFile = args[counter];
      int i = ctFile.lastIndexOf (".");
        
      // If the file has the correct name format, checks its
      // name and extension
      if (i >= 0) {
      	// If the file name corresponds to the class name,
      	// checks the extension
      	String ext = ctFile.substring (i+1);
      	// If the extension is correct, loads the context table
      	// from the file
      	if (ext.equals (CT_EXT)) {
      		System.out.println("==> CT file found: " + ctFile);
      		counter++;
      		t = new ContextTable ();
      		t.loadTable (ctFile);
      		System.out.println("==> CT " + ctFile + " loaded!\n");
      	}
      	//}
      }
    }

    return t;
  }

  /**
   * Loads information from a model saved in a file provided as parameter
   * 
   * @param args the list of parameters provided in the command line
   * @return a model built based on the information from a model file
   */
  private static Model loadModel (String args[], String name) throws ModelException {
  	// Loads the necessary information to reconstruct a model
  	System.out.println("Looking for model file...");
    Model m = null;
    if (counter < argNumber) {
      String mFile = args[counter];
      int i = mFile.lastIndexOf (".");
      if (i >= 0) {
        String ext = mFile.substring (i+1);
        if (ext.equals (MOD_EXT)) {
          System.out.println("==> Model file found: " + mFile);
          counter++;
          m = new Model (name);
          m.loadModel (mFile);
          System.out.println("==> Model " + mFile + " loaded!\n");
        }
      }
    }

    return m;
  }
  
  /**
   * Loads specification from a file whose name is provided as parameter
   * 
   * @param args the list of parameters provided in the command line
   * @param name the name of the file containing the specification
   */
  private static void loadSpec (String args[]) throws FileNotFoundException {
  	// Loads the specification from the indicated file
    System.out.println("Looking for a specification file...");
    //System.out.println ("counter = " + counter);
    //System.out.println ("argNumber = " + argNumber);
    if (counter < argNumber) {
      String spec = args[counter];
      System.out.println("==> Specification file found: " + spec);
      counter++;
      FileReader f = new FileReader (spec);
			specFile = new BufferedReader (f);
			System.out.println("==> Specification successfully loaded!\n");
    }
    else System.out.println("==> No specification loaded!\n");
  }

  /**
   * Presents the help message
   */
  private static void displayUsage () {
    String msg = "\nUsage: java ltse.LTSExtractor [-help] [-traces] ";
    msg += "[<filter_file>] [<refinement_file>] [-c|-t|-e] <log_files> ";
    msg += "[<ct_file>] [<m_file>] [<spec>] ";
    String help_msg = "-help: Displays this help";
    String traces_msg = "-traces: Turns the traces mode on, generating trace files";
    String filter_msg = "filter_file: File containing the list of selected actions";
    String ref_msg = "refinement_file: File containing the list of attributes";
    String c_msg = "-c: Sets actions to represent method calls";
    String t_msg = "-t: Sets actions to represent method terminations";
    String e_msg = "-e: Sets actions to represent method entry and exit points";
    String type_msg = "-prob: Sets model type to probabilistic";
    String log_msg = "log_files: List of files containing the logged information";
    String ct_msg = "ct_file: The name of a file containing context table to be loaded";
    String m_msg = "m_file: The name of a file containing a model to be loaded";
    String s_msg = "spec: The name of a file containing a specification to be loaded";

    System.out.println (msg);
    System.out.println ("");
    System.out.println ("where:");
    System.out.println ("");
    System.out.println (help_msg);
    System.out.println (traces_msg);
    System.out.println (filter_msg);
    System.out.println (ref_msg);
    System.out.println (c_msg);
    System.out.println (t_msg);
    System.out.println (e_msg);
    System.out.println (type_msg);
    System.out.println (log_msg);
    System.out.println (ct_msg);
    System.out.println (m_msg);
    System.out.println (s_msg);
    System.out.println ("");
    System.out.println ("Note: Loading a context table and a model is only possible when processing single-class log files");
    System.exit (0);
  }

  /**
   * Deletes all temporary files 
   */
  private static void deleteTempFiles () {
    FileController fc = new FileController ();
    System.out.println ("Deleting temp files...");
    // Deletes context files
    String curDir = System.getProperty ("user.dir");
    String e = ".ctx";
    if (OSUtils.isWindows())
    	fc.deleteFiles (curDir + "\\", e);
    else
    	//if (OSUtils.isMac())
    		fc.deleteFiles (curDir + "//", e);
    	//else {
    		//System.out.println("Unsupported OS");
    		//System.exit(1);
    	//}
 
    // Deletes temporary log files
    ListIterator<String> l = tempLogs.listIterator ();
    while (l.hasNext ()) {
      String file = l.next ();
      fc.deleteFile (file);
    }
    System.out.println ("==> Temp files deleted!\n");
  }

  public static void main (String args[]) {
   	System.out.println("*** LTSE version: " + Definitions.VERSION + "***");
  	try {
  		// Obtains the total number of arguments
   		argNumber = args.length;
  		// If no argument is provided, raises an exception
  		if (argNumber == 0)
   			throw new ArrayIndexOutOfBoundsException ();
    
  		/*********** OPTIONS PROCESSING ***********/
    		
   		// Checks which options have been selected
   		checkOptions (args);
   		System.out.println("==> Options checked!\n");
    		// Creates filter of actions
   		createFilter (args);
   		System.out.println("==> Filter file processed!\n");
    		// Creates state refiner
   		createRefinement (args);
   		System.out.println("==> Refinement file processed!\n");
   		// Determines mode of action interpretation
   		actionMode (args);
   		System.out.println("==> Action mode set!\n");
   		// Determines model type 
   		modelType(args);
   		String type = null;
   		if (probabilistic) 
   			type = "PROBABILISTIC";
   		else type = "NORMAL";
   		System.out.println ("==> Model type set to " + type + "!\n");

   		/*********** LOG FILES PROCESSING ***********/
    		
    	// Collects list of log files per class
    	Hashtable<String, LinkedList<String>> classLogs = getLogList (args);
    	
    	System.out.println("\n==> Building one model per class found...");
    	Enumeration<String> classes = classLogs.keys();
    	while (classes.hasMoreElements()) {
    		// Obtains the name of the next class in the list 
    		String className = classes.nextElement();
    		
    		if (fullDebugOn) {
    			System.out.println ("class = " + className + "-");
    		} 
    		// Obtains the list of logs for this class
    		LinkedList<String> logs = classLogs.get(className);   
    			
    		/*********** INFORMATION LOADING ***********/
    			
    		// Obtains context table file to be loaded, if it exists
      	ContextTable t = loadContextTable (args, className);
      	if (t == null)
      		System.out.println("\n==> CT file not found for " + className + ". No CT loaded.\n");
      	
      	// Obtains model file to be loaded, if it exists
     		Model m = loadModel (args, className);
     		if (m == null)
     			System.out.println("\n==> Model file not found for " + className + ". No model loaded.\n");
     		
     		// Obtains a specification file to be loaded, if it exists
     		loadSpec (args);
     		
     		// Obtain the current time at the beginning of the model construction process
     		long initialTime = System.currentTimeMillis ();
     		
     		/*********** CONTEXT IDENTIFICATION ***********/
         
     		System.out.println("");
     		System.out.println ("\n\n*************************************" +
        										 "********************************\n");
     		System.out.println ("\n==> Extracting model " + className + "...");
      		
     		// Creates a new context identifier
     		System.out.println ("==> Identifying contexts...\n");
     		ContextAnnotator c = null;
     		if (t != null) 
     			// If a context table has been loaded, use it to identify contexts
     			c = new ContextAnnotator (refiner, filter, mode, t);
     		else 
     			// If no table has been provide, starts with an empty one
     			c = new ContextAnnotator (refiner, filter, mode);
     		
     		// Creates a context table
     		LinkedList<String> ctxFiles = new LinkedList<String> ();
     		t = c.createContextTable (logs, className, ctxFiles);
     		
     		// Displays the context table
        t.showTable ();
        System.out.println("");
        System.out.println("");
           
        System.out.println ("\n==> Contexts identified!");
            
        System.out.println("");
          
        /*********** MODEL CONSTRUCTION ***********/

        // Creates FSP description
        System.out.println ("==> Creating FSP specification...");
        FSPCreator f = null;
        if (m != null) {
         	f = new FSPCreator (m, specFile, probabilistic);
         	className = m.getName ();
        }
        else
        	f = new FSPCreator (className, specFile, probabilistic);

        m = f.parseStateFiles (ctxFiles);
        f.generateFSP (className);
            
        System.out.println ("==> FSP specification generated and saved in file "
                           + className +"."+LTS_EXT+"!\n");
            
        System.out.println("");
        
        if (specFile != null)
        	specFile.close ();
        

        // Obtain the current time at the end of the model construction process
        long endTime = System.currentTimeMillis ();
          
        /*********** PERFORMANCE DATA DISPLAY ***********/

        // Calculates total time spent to produce the model
        long totalTime = endTime - initialTime;
          
        System.out.println ("\n==> Performance information for model " + className + ":\n");
          
        // Displays performance information regarding time and space
        System.out.println ("\nModel extracted in " + totalTime + "ms.");
        if (t != null && m != null) {
         	if (debugOn) {
         		System.out.println ("Context table size: " + t.size () + " bytes");
            System.out.println ("Model structure size: " + m.size ()  + " bytes");
          }

         	int totalSize = t.size () + m.size ();
         	System.out.println ("Approximate memory usage of " + totalSize +
                   					" bytes.");
        }
        else
         	System.out.println ("==> Error measuring memory usage: " + t + " " + m);
          
        /*********** INFORMATION SAVING ***********/

        // Saves the information about this model
        t.saveTable (className);
        f.getModel ().saveModel (className);
    	}
        
      /*********** TEMP FILES ELIMINATION ***********/

      // If not in debug mode, deletes temporary log files
      if (!debugOn && !fullDebugOn)
       	deleteTempFiles ();
           
      System.out.println("");
      System.out.println("");
      
      System.out.println ("\n*****************************************" +
                          "****************************\n\n");
  	}
    	
    // Handles the necessary exceptions
    catch (ArrayIndexOutOfBoundsException e) {
    	displayUsage ();
      if (!debugOn && !fullDebugOn)
      	deleteTempFiles ();
    }
    catch (FilterException e) {
    	System.out.println (e.getMessage ());
    }
    catch (RefinementException e) {
      System.out.println (e.getMessage ());
    }
    catch (LogFileException e) {
    	System.out.println (e.getMessage ());
      if (!debugOn && !fullDebugOn)
      	deleteTempFiles ();
    }
    catch (ContextAnnotationException e) {
    	System.out.println (e.getMessage ());
      if (!debugOn && !fullDebugOn)
       	deleteTempFiles ();
    }
    catch (FSPGenerationException e) {
     	System.out.println (e.getMessage ());
      if (!debugOn && !fullDebugOn)
      	deleteTempFiles ();
    }
    catch (ContextTableException e) {
     	System.out.println (e.getMessage ());
      if (!debugOn && !fullDebugOn)
       	deleteTempFiles ();
    }
    catch (ModelException e) {
     	System.out.println (e.getMessage ());
      if (!debugOn && !fullDebugOn)
      	deleteTempFiles ();
    }
  	catch (FileNotFoundException e) {
     	System.out.println ("Error loading specification file!");
      if (!debugOn && !fullDebugOn)
      	deleteTempFiles ();
    } catch (IOException e) {
    	System.out.println ("Error closing stream to specification file!");
    	if (!debugOn && !fullDebugOn)
      	deleteTempFiles ();
		}
  }
}
