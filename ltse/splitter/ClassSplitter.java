package ltse.splitter;

/**
 * Class that implements the partition of log file into logs for each class.
 *
 * @author Lucio Mauro Duarte
 * @version 18/07/2013
 */

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

import ltse.LTSExtractor;
import ltse.file.Definitions;
import ltse.file.FileController;
import ltse.file.OSUtils;
import ltse.log.LogFileException;

public class ClassSplitter implements Definitions {
	/** Buffer used to read information from the input file */
	private BufferedReader in;
	/** Table of identified instances */
	private Hashtable<String, Hashtable<String,BufferedWriter>> classTable;

	public ClassSplitter () {
		// Creates table of instances
		this.classTable = new Hashtable<String, Hashtable<String,BufferedWriter>> ();
		this.in = null;
	}
	
	/**
	 * Obtains the name of the log file
	 * @param logName the name of the log file
	 * @return the name of the log file
	 */
	private String getName (String logName) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getName("+logName+")");
		}
		
		int j = 0;
		if (OSUtils.isWindows())
			j = logName.lastIndexOf("\\");
		else
			//if (OSUtils.isMac() || )
				j = logName.lastIndexOf("//");
			/*else {
				System.out.println("Unsupported operating system!");
				System.exit (1);
			}*/
		
		String name = new String ();
		if (j > 0) {
			name = logName.substring (j+1);
		}
		else name = logName;
		
		if (LTSExtractor.fullDebugOn) {
			System.out.println("name = " + name);
		}
		
		return name;
	}
	
	/**
	 * Handles each entry from the log file to identify different classes.
	 * @param entry the entry of the log file to be processed
	 * @param name the name of the current log file
	 * @param path the path of the current log file
	 * @throws ClassSplittingException 
	 * @throws IOException 
	 */
	private void handleEntry (String entry, String name) throws ClassSplittingException, IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".handleEntry("+entry+ "," + name  + ")");
		}
		if (entry != null && !entry.equals ("")) {
			// Discovers the class's ID
			String classID = getId (entry);
			
			if (LTSExtractor.fullDebugOn) {
				System.out.println("classID = " + classID);
			}
			
			// Adds the log name based on the class's ID to the list
			// of temporary files
			String classLogName = classID  + "_" + name + "." + LOG_EXT;
			
			if (LTSExtractor.fullDebugOn) {
				System.out.println("classLogName = " + classLogName);
			}
			
			LTSExtractor.tempLogs.add(classLogName);
			BufferedWriter out = null;
			
			// If the annotation contains a class ID...
			if (classID != null) {
				if (this.classTable.containsKey (classID)) {
					// If class already in the table, obtains the 
					// reference to its output buffer
					Hashtable<String, BufferedWriter> hf = this.classTable.get(classID);
					// If output file already exists, recovers the buffered writer
					// associated to it
					if (hf.containsKey(classLogName)) {
						out = getLogFile (classID, classLogName);
					}
					else {
						// Creates buffer to write to file associated with this class
						FileWriter w = new FileWriter (classLogName);
						out = new BufferedWriter (w);
						// Includes new class ID in the table
						(this.classTable.get(classID)).put (classLogName, out);
					}
				}
				else {
					// Creates buffer to write to file associated with this class
					FileWriter w = new FileWriter (classLogName);
					out = new BufferedWriter (w);
					// Includes new class ID in the table
					Hashtable<String, BufferedWriter> classLogTable = new Hashtable<String, BufferedWriter> ();
					classLogTable.put(classLogName, out);
					this.classTable.put (classID, classLogTable);
				}
				// Writes entry on the appropriate log file using the corresponding 
				// output buffer
				out.write (entry);
				out.newLine ();
				out.flush ();
			}
			else throw new ClassSplittingException (classLogName);
		}
	}

	/**
	 * Splits a log file into multiple other files according to annotations
	 * referring to different classes.
	 *
	 * @param logFiles the list of the names of the original log files.
	 *
	 * @return the list of names of log files of each class
	 */
	public Hashtable<String, Enumeration<String>> split (LinkedList<String> logFiles) throws LogFileException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".split("+logFiles+")");
		}
		String logFile = null;
		
		try {
			ListIterator<String> l = logFiles.listIterator();

			while (l.hasNext()) {
				// logFile --> <Path><Name>.log
				logFile = l.next();
				if (LTSExtractor.fullDebugOn) {
					System.out.println("Handling log File = " + logFile + "-");
				}
		
		
				// Obtains the file name
				int i = logFile.lastIndexOf (".");
				// logName --> <Path><Name>
				String logName = logFile.substring (0, i);
				
				if (LTSExtractor.fullDebugOn) {
					System.out.println("logName = " + logName + "-");
				}
				
				// Obtains the name of the original file
				// name --> <Name>
				String name = getName (logName);

				// Removes blank lines and line breaks from annotations
				String fixedLog = name + "-mod." + LOG_EXT;
				//System.out.println ("fixedLog = " + fixedLog + "-");
				removeLineBreaks (logFile, fixedLog);
				
				// Creates buffer to read from the modified log file
				FileReader r = new FileReader (fixedLog);
				this.in = new BufferedReader (r);				
				// Includes entries from the input files in the output files
				String entry = new String ();
				while (entry != null) {
					// Reads the next annotation from the file
					entry = in.readLine ();
					
					if (LTSExtractor.fullDebugOn) {
						System.out.println("Entry = " + entry);
					}
					
					handleEntry (entry, name);
				}
				
				// Removes modified log file
				if (!LTSExtractor.debugOn && !LTSExtractor.fullDebugOn) {
					this.in.close ();
					FileController fc = new FileController ();
					fc.deleteFile (fixedLog);
				}
			}

			// Closes buffers
			this.in.close ();
			closeOutputBuffers ();
		}			
		catch (Exception e) {
			System.err.println ("Error splitting log file " + logFile + " into classes:" + e.getMessage ());
		}
		
		// Associates each log file name to a buffered writer and
		// stores these pairs in a hashtable
		Hashtable<String, Enumeration<String>> files = new Hashtable<String, Enumeration<String>> ();
		Enumeration<String> c = this.classTable.keys();
		while (c.hasMoreElements()) {
			String cid = c.nextElement();
			Hashtable<String, BufferedWriter> nh = classTable.get(cid);
			Enumeration<String> classKeys = nh.keys();
			
			if (LTSExtractor.fullDebugOn) {
				System.out.println("cid = " + cid + ", keys = " + classKeys);
			}
			
			files.put (cid, classKeys);
		}

		return files;
	}

	/**
	 * Retrieves the ID of the class from an annotation.
	 *
	 * @param annotation the annotation for which the class ID is required.
	 *
	 * @return the class ID.
	 */
	private String getId (String annotation) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getId("+annotation+")");
		}
		
		String e = new String (annotation);
		String id = null;

		int i = e.indexOf (LABEL_SEP);
		if (i > 0) {
			String type = e.substring (0, i);
			
			if (LTSExtractor.fullDebugOn) {
				System.out.println ("type = " + type);
			}
			
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
				i = e.indexOf (CLASS_SEP);
				if (type.equals (REP_ENTER) || type.equals (SEL_ENTER)) {
					// Skips value of control predicate
					int j = e.indexOf(SEP);
					e = e.substring (j+1);
					i = e.indexOf (CLASS_SEP);
				}
				// Collects the class name
				id = e.substring (0, i);
			}

			if (type.equals (ACTION)) {
				// Skips action name
				e = e.substring (i+1);
				i = e.indexOf (SEP);
				e = e.substring (i+1);
				i = e.indexOf (SEP);
				e = e.substring (i+1);
				i = e.indexOf (CLASS_SEP);
				// Collects the class name
				id = e.substring (0, i);
			}
		}
		
		id = id.trim ();
		if (LTSExtractor.fullDebugOn) {
			System.out.println ("id = " + id + "-");
		}

		return id;
	}

	/**
	 * Returns a reference to log file used for a specific class.
	 *
	 * @param id the instance ID.
	 *
	 * @return a reference to a buffer which will be used to write on the file.
	 */
	private BufferedWriter getLogFile (String classID, String logName) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getLogFile("+classID+","+logName+")");
		}
		Hashtable<String, BufferedWriter> lf = this.classTable.get (classID);
		BufferedWriter b = lf.get(logName);
		return b;
	}

	/**
	 * Closes all buffers used to write on log files.
	 */
	private void closeOutputBuffers () throws IOException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".closeOutputBuffers()");
		}
		Enumeration<Hashtable <String, BufferedWriter>> lf = this.classTable.elements ();
		while (lf.hasMoreElements ()) {
			Hashtable<String, BufferedWriter> lh = lf.nextElement();
			Enumeration<BufferedWriter> o = lh.elements();
			while (o.hasMoreElements()) {
				BufferedWriter b = o.nextElement ();
				b.close () ;
			}
		}
	}
	
	/**
	 * Remove blank spaces from class name.
	 * @param name the original name of the class
	 * @return the name of the class without blank spaces
	 */
	/*private String removeBlankSpace (String name) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".removeBlankSpace ("+name+")");
		}
		
		int i = -1;
		
		do {
			i = name.indexOf (" ");
			
			if (i > -1)
				if (i < (name.length () - 1))
					name = name.substring (0, i) + name.substring (i+1);
				else name = name.substring (0, i);
			
		} while (i > -1);
		
		return name;
	}*/
	
	/**
	 * Removes line breaks and blank lines from annotations in a log file and outputs the result in
	 * another log file
	 * @param inputFile the name of the log file containing the annotations
	 * @param outputFile the name of the log file which the annotations without line breaks
	 * will be copied to
	 * @throws IOException 
	 */
	private void removeLineBreaks (String inputFile, String outputFile) throws IOException{
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".removeLineBreaks("+inputFile+","+outputFile+")");
		}
			
		// Creates reader for the input file
		FileReader r = new FileReader (inputFile);
		BufferedReader in = new BufferedReader (r);
		
		// Creates writer for the output file
		FileWriter w = new FileWriter (outputFile);
		BufferedWriter b = new BufferedWriter (w);
			
		String s = new String ();
		// Checks whether the file has any more lines to be read
		while (s != null) {
			String cmd = new String ();
			do {
				// Reads the next line from the input file
				s = in.readLine();
				// If a new line is found, concatenates this with what has been
				// read before
				if (s != null) {
					//s = removeSpaces (s);
					
					cmd += s;
				}
				// If no line found, exits the loop
				else break;
				// Repeats the loop until it finds the end-of-annotation marker
			} while (!s.endsWith(END));
			
			// Removes the end-of-annotation marker
			if (/*!cmd.equals (END_STATE) &&*/ (s != null)) {
				int i = cmd.lastIndexOf(END);
				cmd = cmd.substring(0, i);
			}
			
			// Writes modified annotation to output file
			b.write (cmd);
			b.flush();
			b.newLine();
		}
		
		// Closes the buffers
		in.close();
		b.close();
	}
	
	/*public static void main (String args[]) {
		ClassSplitter c = new ClassSplitter ();
		
		Enumeration<String> e = c.split("D:\\Documentos\\workspace\\JavaPlayer\\src\\JP-cpy.log");
		while (e.hasMoreElements()) {
			System.out.println (e.nextElement());
		}
		String s = c.removeSpaces("CALL_ENTER:getPath#JavaPlayer=19543842#MP3FilePath:				D:\\documentos\\musicas\\Paty indignado.mp3FileSize:			155826 bytesPlayingTime:			2:35MPEG Version 2.0 Layer IIIBitRate:			8kbpsSampleRate:			22050HzChannelMode:			Single Channel (MONO)Copyrighted:			falseOriginal:			trueCRC:				falseEmphasis:			noneID3v1.0TagSize:			128 bytesTitle:				PatyArtist:				Album:				Year:				Comment:			Track:				3Genre:				BluesID3v2.3.0TagSize:			211 bytesUnsynchronisation:		falseExtended Header:		falseExperimental:			falseFooter:				falsePadding:			0 bytesTRCKTagAlterDiscard:		falseFileAlterDiscard:		falseReadOnly:			falseGrouped:			falseCompressed:			falseEncrypted:			falseUnsynchronised:			falseLengthIndicator:		falseData:				3TCOPTagAlterDiscard:		falseFileAlterDiscard:		falseReadOnly:			falseGrouped:			falseCompressed:			falseEncrypted:			falseUnsynchronised:			falseLengthIndicator:		falseData:				TALBTagAlterDiscard:		falseFileAlterDiscard:		falseReadOnly:			falseGrouped:			falseCompressed:			falseEncrypted:			falseUnsynchronised:			falseLengthIndicator:		falseData:				COMMTagAlterDiscard:		falseFileAlterDiscard:		falseReadOnly:			falseGrouped:			falseCompressed:			falseEncrypted:			falseUnsynchronised:			falseLengthIndicator:		falseData:				#{playlist=#EXTM3U#EXTINF:155,Paty indignado - PatyD:\\documentos\\musicas\\Paty indignado.mp3#EXTINF:73,Italian To Malta - Italian to MaltaD:\\documentos\\musicas\\Italian To Malta.mp3#EXTINF:77,susto_milton_neves - Milton NevesD:\\documentos\\musicas\\susto_milton_neves.mp3^player=null^mainPanel=javax.swing.JPanel[,0,0,450x25,layout=java.awt.FlowLayout,alignmentX=0.0,alignmentY=0.0,border=javax.swing.border.BevelBorder@e1d5ea,flags=9,maximumSize=,minimumSize=,preferredSize=]^progressBar=javax.swing.JProgressBar[,153,4,270x18,alignmentX=0.0,alignmentY=0.0,border=javax.swing.plaf.BorderUIResource$LineBorderUIResource@29ab3e,flags=8,maximumSize=,minimumSize=,preferredSize=java.awt.Dimension[width=270,height=18],orientation=HORIZONTAL,paintBorder=true,paintString=true,progressString=,indeterminateString=false]^fileTitle=Paty^playThread=null^}#76");
		String s = c.removeSpaces ("adgd       ajdgasdgsahd    d safdjhj adsad  d  s   JUJKJ   a");
		System.out.println(s);
		
		String t1 = c.removeBlankSpace ("Editor ");
		System.out.println ("t1 = -" + t1 + "-");
		String t2 = c.removeBlankSpace ("Ed itor");
		System.out.println ("t2 = -" + t2 + "-");
		String t3 = c.removeBlankSpace (" Editor");
		System.out.println ("t3 = -" + t3 + "-");
		String t4 = c.removeBlankSpace (" E d i t o r ");
		System.out.println ("t4 = -" + t4 + "-");
	}*/
}
