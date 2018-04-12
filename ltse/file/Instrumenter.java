package ltse.file;

/**
 * Class that creates a batch file to instrument a Java source code.
 *
 * @author Lucio Mauro Duarte
 * @version 09/12/2015
 */

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Creates a batch files to include and remove annotations from a Java file.
 */
public class Instrumenter implements Definitions {
	
	/**
	 * Creates instrumentation file.
	 * 
	 * @param os the current OS
	 * @param instrumentFile the name of the instrumentation file
	 * @param fileNames the names of the files to be instrumented
	 * @throws IOException 
	 */
	private static void createInstrumentationFile (String instrumentFile, Vector<String> fileNames) throws IOException {
		// Checks the OS type and creates the file using the appropriate
		// format
		if (OSUtils.isWindows()) 
			createWinInstrumentationFile (instrumentFile, fileNames);
		else
			//if (OSUtils.isMac())
				createMACInstrumentationFile (instrumentFile, fileNames);
			//else
				//System.out.println("Unsupported operating system");
	}
	
	/**
	 * Creates an instrumentation file for a machine running Windows OS.
	 * 
	 * @param instrumentFile the name of the instrumentation file
	 * @param fileNames the names of the files to be instrumented
	 * @throws IOException 
	 */
	private static void createWinInstrumentationFile (String instrumentFile, Vector<String> fileNames) throws IOException {
		// Creates necessary write streams
		FileWriter f = new FileWriter (instrumentFile);
		BufferedWriter out = new BufferedWriter (f);
		
		out.write ("@echo off");
		out.newLine ();
		out.newLine ();

		out.write ("echo Starting instrumentation...");
		out.newLine ();

		// Creates a temp folder
		out.write ("mkdir temp");
		out.newLine ();
		out.newLine ();

		// Instruments each file in the enumeration
		Enumeration<String> e = fileNames.elements ();
		while (e.hasMoreElements ()) {
			String originalName = e.nextElement ();

			out.newLine ();
			out.newLine ();
			out.write ("echo Inserting annotations in " + originalName);
			out.newLine ();
			out.newLine();
			
			// Obtains path to the original file
			String filePath = ".\\";
			int k = originalName.lastIndexOf ("\\");
			if (k > -1)
				filePath = originalName.substring (0, k);

			// Creates output file name
			String fileName = createOutputName (originalName);
			fileName = fileName.substring (k+1);
			String newName = fileName + "_original.java";

			// Renames original file
			out.write ("ren " + originalName + " " + newName);
			out.newLine ();
			// Moves original file to the temp folder
			out.write ("copy " + filePath + "\\" + newName + " temp");
			out.newLine ();
			out.newLine();

			out.write ("@echo off");
			out.newLine ();
			out.newLine ();

			// Instruments the Java file
			out.write ("txl -i \"%INSTR_PATH%\\rules\" -in 2 -o " + originalName + " " );
			out.write (newName + " " + "\"" + 
					"%INSTR_PATH%\\rules\\JavaRules.txl" + "\""); /* CHANGED */
			out.newLine ();
			out.newLine();

			// Removes temporary file
			out.write ("del " + filePath + "\\" + newName);
			out.newLine ();
			
			out.write ("echo " + originalName + " succesfully instrumented!");
			out.newLine ();
			out.newLine ();
		}
		
		// Cleans stream
		out.flush ();
		// Closes stream
		out.close ();
	}

	/**
	 * Creates an instrumentation file for a machine running MAC OS X.
	 * 
	 * @param instrumentFile the name of the instrumentation file
	 * @param fileNames the names of the files to be instrumented
	 * @throws IOException 
	 */
	private static void createMACInstrumentationFile (String instrumentFile, Vector<String> fileNames) throws IOException {
		// Creates necessary write streams
		FileWriter f = new FileWriter (instrumentFile);
		BufferedWriter out = new BufferedWriter (f);
		
		// Creates bash file
		out.write ("#! /bin/bash");
		out.newLine ();
		out.newLine ();

		out.write ("echo Starting instrumentation...");
		out.newLine ();
		
		out.write ("stty -echo");
		out.newLine ();
		out.newLine ();

		// Creates a temp folder
		out.write ("mkdir temp");
		out.newLine ();
		out.newLine ();

		// Instruments each file in the enumeration
		Enumeration<String> e = fileNames.elements ();
		while (e.hasMoreElements ()) {
			String originalName = e.nextElement ();

			out.write ("stty echo");
			out.newLine ();
			out.newLine ();
			out.write ("echo Inserting annotations in " + originalName);
			out.newLine ();
			out.newLine();
			out.write ("stty -echo");
			out.newLine ();
			out.newLine ();

			// Creates output file name
			String fileName = createOutputName (originalName);
			String newName = fileName + "_original.java";
			
			// Copies original file to new file
			out.write ("cp " + originalName + " " + newName);
			out.newLine ();
			out.newLine();

			// Instruments the Java file
			out.write ("txl -i \"$INSTR_PATH/rules\" -in 2 -o " + originalName + " ");
			out.write (newName +  " " + "$INSTR_PATH/rules/JavaRules.txl"); /* CHANGED */
			out.newLine ();
			out.newLine();
			
			// Moves original file to the temp folder
			out.write ("mv " + newName + " temp");
			out.newLine ();
			out.newLine();
			
			out.write ("stty echo");
			out.newLine ();
			out.newLine ();
			out.write ("echo " + originalName + " succesfully instrumented!");
			out.newLine ();
			out.newLine ();
		}
		
		// Cleans stream
		out.flush ();
		// Closes stream
		out.close ();
	}
	
	/**
	 * Creates the restore file, which reverts instrumentation.
	 * 
	 * @param os the current operating system
	 * @param restoreFile the restore file name
	 * @param fileNames the names of the files to be restored
	 * @throws IOException 
	 */
	private static void createRestoreFile (String restoreFile, Vector<String> fileNames) throws IOException {
		// Checks the OS type and creates the file using the appropriate
		// format
		if (OSUtils.isWindows()) 
			createWinRestoreFile (restoreFile, fileNames);
		else
			//if (OSUtils.isMac())
				createMACRestoreFile (restoreFile, fileNames);
			//else
				//System.out.println("Unsupported operating system");
	}
	
	/**
	 * Creates the restore file, which reverts instrumentation.
	 * 
	 * @param os the current operating system
	 * @param restoreFile the restore file name
	 * @param fileNames the names of the files to be restored
	 * @throws IOException 
	 */
	private static void createWinRestoreFile (String restoreFile, Vector<String> fileNames) throws IOException {
		// Creates necessary streams
		FileWriter f = new FileWriter (restoreFile);
		BufferedWriter out = new BufferedWriter (f);

		out.write ("@echo off");
		out.newLine ();
		out.newLine ();

		Enumeration<String> e = fileNames.elements ();
		while (e.hasMoreElements ()) {
			String originalName = e.nextElement ();

			out.write ("echo Restoring " + originalName);
			out.newLine ();
			out.write ("@echo off");
			out.newLine ();
			
			// Re-creates the original file name 
			String fileName = createOutputName (originalName);
			String newName = fileName + "_original.java";

			// Obtains path to the file
			String path = ".\\";
			int p = originalName.lastIndexOf ("\\");
			if (p > -1)
				path = originalName.substring (0, p);

			// Obtains name without path
			String simpleName = originalName.substring (p+1);
			int q = newName.lastIndexOf ("\\");
			String simpleNewName = newName.substring (q+1);

			// Deletes instrumented file
			out.write ("del " + originalName);
			out.newLine ();

			// Restores original file
			out.write ("cd temp");
			out.newLine ();
			out.write ("copy " + simpleNewName + " ..\\" + path + "\\" + simpleNewName);
			out.newLine ();
			out.write ("del " + simpleNewName);
			out.newLine ();
			out.write ("cd ..");
			out.newLine ();
			out.write ("ren " + path + "\\" + simpleNewName + " " + simpleName);
			out.newLine ();
			out.newLine ();
			
			out.write ("echo " + originalName + " succesfully restored!");
			out.newLine ();
			out.newLine ();
		}
		
		// Removes temp folder
		out.write ("rmdir temp");

		// Cleans stream
		out.flush ();
		// Closes stream
		out.close ();
	}
	
	/**
	 * Creates the restore file, which reverts instrumentation.
	 * 
	 * @param os the current operating system
	 * @param restoreFile the restore file name
	 * @param fileNames the names of the files to be restored
	 * @throws IOException 
	 */
	private static void createMACRestoreFile (String restoreFile, Vector<String> fileNames) throws IOException {
		// Creates necessary streams
		FileWriter f = new FileWriter (restoreFile);
		BufferedWriter out = new BufferedWriter (f);
		
		// Creates bash file
		out.write ("#! /bin/bash");
		out.newLine ();
		out.newLine ();

		Enumeration<String> e = fileNames.elements ();
		while (e.hasMoreElements ()) {
			String originalName = e.nextElement ();

			out.write ("echo Restoring " + originalName);
			out.newLine ();
			
			out.write ("stty -echo");
			out.newLine ();
			out.newLine ();
			
			// Re-creates the original file name 
			String fileName = createOutputName (originalName);
			String newName = fileName + "_original.java";

			// Restores original file
			out.write ("cp temp/" + newName + " " + originalName);
			out.newLine ();
			out.newLine ();
			
			out.write ("stty echo");
			out.newLine ();
			out.newLine ();
			out.write ("echo " + originalName + " succesfully restored!");
			out.newLine ();
			out.newLine ();
		}
		
		// Removes temp folder
		out.write ("cd temp");
		out.newLine ();
		out.write ("rm *");
		out.newLine ();
		out.write ("cd ..");
		out.newLine ();
		out.write ("rmdir temp");

		// Cleans stream
		out.flush ();
		// Closes stream
		out.close ();
	}
	
	/**
	 * Creates a batch file to execute the instrumented Java files.
	 *
	 * @param fileNames the names of the original Java files
	 * @param name the name of the main class
	 * @param applet indicates whether the application is an applet or not
	 * @param args the arguments of the application
	 * @param os indicates the operating system where the instrumentation will be run
	 * 
	 * @return the names of the created batch files
	 */
	private static String[] createBatchFiles (Vector<String> fileNames, String name) {
		
		String instrumentFile = null;
		String restoreFile = null;

		// Obtains the name of the main class without the path
		String className = name;
		int j = -1;
		
		if (OSUtils.isWindows())
			j = name.lastIndexOf ("\\");
		else 
			j = name.lastIndexOf("//");

		if (j > -1) {
			className = className.substring (j+1);
		}

		// Removes names of packages and extension, if they exist
		// Creates batch file name based on class name
		String batchName = createClassOutputName (className);;

		try {
			// Sets batch instrumentation file name
			if (OSUtils.isWindows())
				instrumentFile = batchName + "-instrument.bat";
			else 
				//if (OSUtils.isMac())
					instrumentFile = batchName + "-instrument.sh";
			
			createInstrumentationFile (instrumentFile, fileNames);
						
			String files = new String ();
			Enumeration<String> e = fileNames.elements();
			while (e.hasMoreElements()) {
				files = files + " " + e.nextElement();
			}
			
			// Sets batch restore file name
			if (OSUtils.isWindows())
				restoreFile = batchName + "-restore.bat";
			else 
				//if (OSUtils.isMac())
					restoreFile = batchName +"-restore.sh";
			
			createRestoreFile (restoreFile, fileNames);
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
		
		// Saves instrumentation and restore file names to return this information
		String[] batchFiles = new String[2]; 
		batchFiles[0] = instrumentFile;
		batchFiles[1] = restoreFile;

		return batchFiles;
	}

	/**
	 * Obtains the name of the original file without the extension.
	 *
	 * @param fileName the name of the original file
	 *
	 * @return the file name without the extension
	 */
	private static String createOutputName (String fileName) {
		int i = 0;
  	int j = 0;

  	do {
  		i = fileName.indexOf (".");
  		j = fileName.lastIndexOf (".");

  		if (i != j)
  			fileName = fileName.substring (i+1);
  	} while (i != j);

		// Obtains the name of the file without the extension
		if (j > -1)
			fileName = fileName.substring (0, j);

		return fileName;
	}

	/**
	 * Obtains the name of the class without the past.
	 *
	 * @param className the name of the class
	 *
	 * @return the file name without the extension
	 */
	private static String createClassOutputName (String className) {
		int i = className.lastIndexOf (".");

		if (i > 0) {
			className = className.substring (i+1);
		}
  	
		return className;
	}


	/**
	 * Checks whether a given file name is already in the list of files 
	 *  to be instrumented.
	 *  
	 * @param files the list of files to be instrumented
	 * @param s the file name target of the search
	 * 
	 * @return <code>true</code> if the list contains the name and <code>false<\code>
	 *         otherwise
	 */
	private static boolean isIn (Vector<String> files, String s) {
		Enumeration<String> e = files.elements ();
		while (e.hasMoreElements()) {
			String aux = e.nextElement ();
			if (aux.compareTo(new String(s)) == 0)
				return true;
		}
		return false;
	}
	
	/**
	 * Replaces a string by another in a given file and writes the result into
	 * the output file.
	 * 
	 * @param fileName the original file
	 * @param f the outputstream used to write the results to the output file
	 * @param oldStr the string to be replaced
	 * @param newStr the string to used as the replacement
	 */
	public static void replaceString (String fileName, BufferedWriter f, String oldStr, String newStr) throws IOException {
		f.write("if \"" + oldStr + "\"==\"\" findstr \"^::\" \"%~f0\"&GOTO:EOF");
		f.newLine();
		f.write("for /f \"tokens=1,* delims=]\" %%A in ('\"type " + fileName + "|find /n /v \"\"\"') do (");
		f.newLine();
		f.write("set \"line=%%B\"");
		f.newLine();
		f.write("if defined line (");
		f.newLine();
		f.write("call set \"line=echo.%%line:" + oldStr + "=" + newStr + "%%\"");
		f.newLine();
		f.write("for /f \"delims=\" %%X in ('\"echo.\"%%line%%\"\"') do %%~X >> "+ fileName + "_temp.java");
		f.newLine();
		f.write(") ELSE echo.)");
	}
	
	/*
	 * Displays the help message.
	 */
	private static void displayUsage () {
		String msg = "\nUsage: java [-help] ltse.file.Instrumenter <system_name> ";
		msg += "-f <java_files>";
		String help_msg = "-help: Displays this help";
		String main_msg = "system_name: The name of the system under consideration";
		String files_msg = "java_files: Java files to be instrumented";

		System.out.println (msg);
		System.out.println ("");
		System.out.println ("where:");
		System.out.println ("");
		System.out.println (help_msg);
		System.out.println (main_msg);
		System.out.println (files_msg);
		System.out.println ("");
	}
	
	/**
	 * The main method
	 * @param args the name of the system and the names of the files to instrument
	 */
	public static void main (String args[]) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  	Calendar cal = Calendar.getInstance();
		System.out.println("*** Instrumenter version: " + dateFormat.format(cal.getTime()) + "***");
		try {
			int tam = args.length;

			if (tam == 0)
				throw new ArrayIndexOutOfBoundsException ();

			Vector<String> files = new Vector<String> ();
			int counter = 0;
			
			// Checks whether help is required
			String h = args[counter];
			if (h.equals ("-help")) {
				displayUsage ();
				System.exit (0);
			}

			// Name of the project, used in the name of the produced files
			String name = args[counter];
			counter++;

			// Obtains names of the Java files to be instrumented
			if (counter < tam) {
				if (args[counter].equals ("-f")) {
					counter++;
					while ((counter < tam) && (!args[counter].equals ("-n"))) {
						String s = args[counter];
						if (!isIn (files, s))
							files.add (s);
						counter++;
					}
				}
				else
					throw new ArrayIndexOutOfBoundsException ();
			}
			else
				throw new ArrayIndexOutOfBoundsException ();
						
			// Calls the generation of a batch file to instrument the necessary Java
			// files
			System.out.println ("Creating batch file...");
			String[] batchFiles = Instrumenter.createBatchFiles (files, name);
			System.out.println ("Batch files created!");
			System.out.print ("Execute " + batchFiles[0] + " to instrument the application code\n");
			System.out.print ("Execute " + batchFiles[1] + " to remove instrumentation code\n");
		}
		catch (ArrayIndexOutOfBoundsException e) {
			displayUsage ();
		}
	}
}