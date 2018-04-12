package ltse.context;

/**
 * Class that implements a context table.
 *
 * @author Lucio Mauro Duarte
 */

import java.util.LinkedList;
import java.util.ListIterator;
import java.io.*;

import ltse.LTSExtractor;
import ltse.file.Definitions;

/**
 * Table to store context information
 */
public class ContextTable implements Definitions {
	/** Context table */
	private LinkedList<TableEntry> table;

	/**
	 * Creates a new context table.
	 */
	public ContextTable () {
		table = new LinkedList<TableEntry> ();
	}

	
	/**
	 * Adds a new entry to the table.
	 *
	 * @param s the state number
	 * @param p the predicate tested
	 * @param v the evaluation of predicate p
	 * @param i the block ID
	 * @param a the set of values of attributes associated to this entry
	 * @param cs the call stack
	 */
	protected void add (int s, String p, String v, int i, String a, String cs) {
		if (LTSExtractor.fullDebugOn) { 
			System.out.println(this.getClass().getName() + ".add("+s+","+p+","+v+","+i+","+a+","+cs+")");
		}
		TableEntry e = new TableEntry (s, p, v, i, a, cs);
		table.add (e);
	}
	
	/**
	 *	Tests whether an event is already in the table.
	 *
	 * @param t the test associated to the event
	 * @param v the value of the test
	 * @param a the set of attributes associated to this entry
	 * @param id the block ID
	 * @param cs the call stack 
	 *
	 * @return <code>true<\code> if event is in the table and
	 *         <code>false<\code>, otherwise
	 */
	protected boolean isInTable (String t, String v, String a, int id, String cs) {
		if (LTSExtractor.fullDebugOn) { 
			System.out.println(this.getClass().getName() + ".isInTable("+t+","+v+","+a+","+id+","+cs+")");
		}
		int i = 0;
		TableEntry e = null;

		// Compares each element already in the table with the element
		// to be inserted
		while (i < table.size ()) {
			
			e = table.get (i);
			String te = e.getPredicate ();
			int ide = e.getId ();
			String ve = e.getValue ();
			String ae = e.getAttributes ();
			String cse = e.getCallStack();

			if (te.equals (t)) {
				if (ide == id) {
					if (ve.equals (v)) {
						if (ae.equals (a)) {
							if (cse.equals(cs)) {
								return true;
							}
							//else System.out.println (cse + " = "+ cs);
						}
						//else System.out.println (ae + " = "+ a);
					}
					//else System.out.println (ve + " = "+ v);
				}
				//else System.out.println (ide + " = "+ id);
			}
			//else System.out.println (te + " = "+ t);

			i++;
		}
		// If any of the tests above fail, then they are different
		return false;
	}

	/**
	 * Obtains the last state number used in the context table.
	 *
	 * return the last state number found in the table.
	 */
	public int getNextState () {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getLastState()");
		}
		return table.size();
	}

	/*
	 * Recovers the state corresponding to the context defined
	 * by the arguments.
	 * 
	 * @param v the value of the control predicate
	 * @param a the set of values of the attributes
	 * @param id the block ID
	 * 
	 * @return the state number
	 */
	public int getState (String v, String a, int id) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getState("+v+","+a+","+id+")");
		}
		int i = 0;
		boolean found = false;
		TableEntry e = null;
		int state = -1;

		while ((i < table.size ()) && !found) {

			e = table.get (i);

			if (e.getValue ().equals (v))
				if (e.getAttributes ().equals (a))
					if (e.getId () == id) {
						state = e.getState ();
						found = true;
					}

			i++;
		}

		return state;
	}
	
	//Version with call stack
	public int getState (String v, String a, int id, String cs) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getState("+v+","+a+","+id+","+cs+")");
		}
		int i = 0;
		boolean found = false;
		TableEntry e = null;
		int state = -1;

		while ((i < table.size ()) && !found) {

			e = table.get (i);

			if (e.getValue ().equals (v))
				if (e.getAttributes ().equals (a))
					if (e.getId () == id)
						if (e.getCallStack().equals(cs))		{
							state = e.getState ();
							found = true;
						}

			i++;
		}

		return state;
	}

	/*
	 * Returns the predicate that defines the context
	 * related to a certain state.
	 * 
	 * @param s the state number
	 * 
	 * @return the predicate description
	 */
	public String getPredicate (int s) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getPredicate("+s+")");
		}
		int i = 0;
		boolean found = false;
		TableEntry e = null;
		String t = null;

		while ((i < table.size ()) && !found) {

			e = table.get (i);

			if (e.getState () == s) {
				t = e.getPredicate ();
				found = true;
			}
			else
				i++;
		}

		return t;
	}

	/*
	 * Returns the attributes that define the context
	 * related to a certain state.
	 * 
	 * @param s the state number
	 * 
	 * @return the list of attributes and their respective values
	 */
	public String getAttributes (int s) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".getAttribute("+s+")");
		}
		int i = 0;
		boolean found = false;
		TableEntry e = null;
		String attribs = null;

		while ((i < table.size ()) && !found) {

			e = table.get (i);

			if (e.getState () == s) {
				attribs = e.getAttributes ();
				found = true;
			}
			else
				i++;
		}

		return attribs;
	}

	/**
	 * Creates a title to be exhibited with the contents of one column of the table according
	 * to the length of the values of the column.
	 *
	 * @param i the length of the largest value of the column
	 * @param l the length of the shortest value of the column
	 * @param t the title of the column
	 */
	private String createTitle (int i, int l, String t) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".createTitle("+i+","+l+","+t+")");
		}
		int d = i - l;
		for (int c = 0; c < d; c++)
				t += " ";

		return t;
	}

	/**
	 * Formats a value of a column of the table to be exhibited.
	 *
	 * @param l the length of the value to exhibited
	 * @param info the value to be exhibited
	 */
	private String formatInfo (int l, String info) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".formatInfo("+l+","+info+")");
		}
		int d = l - info.length ();
		for (int c = 0; c < d; c++)
			info += " ";

		return info;
	}

	/**
	 * Shows the information contained in the context table
	 */
	public void showTable () {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".showTable()");
		}

		int p = 1;
		int b = 1;
		int v = 1;
		int a = 1;

		for (int i = 0; i < table.size (); i++) {

			TableEntry e = table.get (i);

			if (e.getPredicate ().length () > p)
				p = e.getPredicate ().length ();

			String bid = e.getId () + "";

			if (bid.length () > b)
				b = bid.length ();

			if (e.getValue ().length () > v)
				v = e.getValue ().length ();

			if (e.getAttributes ().length () > a)
				a = e.getAttributes ().length ();
		}

		String pTitle = createTitle (p, 1, "P");
		String bTitle = createTitle (b, 1, "ID");
		String vTitle = createTitle (v, 1, "V");
		String aTitle = createTitle (a, 1, "A");

		System.out.print ("S   ");
		System.out.print (pTitle + "  ");
		System.out.print (bTitle + "  ");
		System.out.print (vTitle + "  ");
		System.out.print (aTitle + "  ");
		System.out.print ("CS");
		System.out.println ("");

		for (int i = 0; i < table.size (); i++) {

			TableEntry e = table.get (i);

			String pred = e.getPredicate ();
			if (pred.length () == 0)
				pred = "-";
			pred = formatInfo (p, pred);

			String val = e.getValue ();
			if (val.equals ("true"))
				val = "T";
			else
				if (val.equals ("false"))
					val = "F";

			val = formatInfo (v, val);

			int state = e.getState ();
			String s = (new Integer (state)).toString ();
			if (state < 10)
				s += " ";

			String attr = e.getAttributes ();
			attr = formatInfo (a, attr);
			System.out.print (s + "  ");
			System.out.print (pred + "  ");
			String id = e.getId () + "";
			int diff = b - id.length ();
			System.out.print (e.getId () + "   ");
			for (int j = 0; j < diff; j++)
					System.out.print (" ");
			System.out.print (val);
			if (!e.getAttributes ().equals ("{") && !e.getAttributes ().equals ("{}"))
				System.out.print (attr + "  ");
			else
				for (int k = 0; k < (a + 2); k++)
					System.out.print (" ");
			System.out.print (e.getCallStack());
			System.out.println ("");
		}
	}

	/**
	 * Saves the contents of the table in a file.
	 *
	 * @param fileName the name of the file where the table contents are to be saved in
	 */
	public void saveTable (String fileName) throws ContextTableException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".saveTable("+fileName+")");
		}
		System.out.println ("\n==> Saving context table...");
		try {
			FileWriter f = new FileWriter (fileName.trim () + "."+CT_EXT);
			BufferedWriter out = new BufferedWriter (f);

			ListIterator<TableEntry> i = table.listIterator ();
			while (i.hasNext ()) {
				TableEntry e = i.next ();

				int s = e.getState ();
				String p = e.getPredicate ();
				/*int j = p.indexOf("@");
				if (j > -1)
					p = p.substring(0, j);*/
				String v = e.getValue ();
				String a = e.getAttributes ();
				int id = e.getId ();
				String c = e.getCallStack ();

				out.write (s+SEP+p+SEP+id+SEP+v+SEP+a+SEP+c);
				out.newLine ();
				out.flush ();
			}

			out.close ();
			System.out.println ("The context table has been saved as " + fileName + "."+CT_EXT+"\n");
		}
		catch (Exception e) {
			throw new ContextTableException ("Error saving context table: " + e.getMessage ());
		}
	}

	/**
	 * Loads the contents from a table saved in a file.
	 *
	 * @param fileName the name of the file where the table contents are to be loaded from
	 */
	public void loadTable (String fileName) throws ContextTableException {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + ".loadTable("+fileName+")");
		}
		System.out.println ("==> Loading context table...\n");
		try {
			FileReader f = new FileReader (fileName);
			BufferedReader in = new BufferedReader (f);

			String entry = new String ();
			while (entry != null) {
				entry = in.readLine ();
			//	System.out.println("entry = " + entry);


				if (entry != null) {
					int i = entry.indexOf (SEP);
					int s = Integer.parseInt (entry.substring (0, i));
					//System.out.println("s = " + s);
					entry = entry.substring (i + 1);

					i = entry.indexOf (SEP);
					String p = "";
					if (i > -1)
						p = entry.substring (0, i);
					//System.out.println("p = " + p);
					entry = entry.substring (i + 1);

					i = entry.indexOf (SEP);
					int id = Integer.parseInt (entry.substring(0, i));
					//System.out.println("id = " + id);
					entry = entry.substring (i + 1);

					i = entry.indexOf (SEP);
					String v = entry.substring (0, i);
					//System.out.println("v = " + v);
					entry = entry.substring (i + 1);
					
					i = entry.indexOf (SEP);
					String a = entry.substring (0, i);
					//System.out.println("v = " + v);
					entry = entry.substring (i + 1);

					String cs = entry;
					//System.out.println("a = " + a);

					add (s, p, v, id, a, cs);
				}
			}

			in.close ();
			System.out.println ("The context table has been loaded from " + fileName);
		}
		catch (Exception e) {
			throw new ContextTableException ("Error loading context table: " + e.getMessage ());
		}
	}

	/**
	 * Returns the approximate size of the table in memory.
	 *
	 * @return the approximate size in bytes of the table in memory.
	 */
	public int size () {
		int i = 0;
		TableEntry e = null;
		int tableSize = 0;

		// Compares each element already in the table with the element
		// to be inserted
		while (i < table.size ()) {
			e = table.get (i);
			tableSize += e.size ();
			i++;
		}
		return tableSize;
	}
	
	public static void main (String args[]) {
		ContextTable ct = new ContextTable ();
		try {
			ct.loadTable ("/Users/lucioduarte/Dropbox/MyWorkspace/TrafficLights/Models/TrafficLights.ctb");
			ct.showTable ();
			
			String t = "(GREEN)";
			String v = "0";
			String a = "{isGreen=false}";
			int id = 9;
			String cs = "<>";
			
			if (ct.isInTable (t, v, a, id, cs))
				System.out.println ("In Table!");
			else System.out.println ("ERROR!");
			
			v = "1";
			
			if (ct.isInTable (t, v, a, id, cs))
				System.out.println ("ERROR!");
			else
				System.out.println ("NOT in Table!");
			
		} catch (ContextTableException e) {
			
			e.printStackTrace();
		}
	}
}

// Entry of context table
class TableEntry {
	private int state;
	private String predicate;
	private String value;
	private String attributes;
	private int id;
	private String cs;
	
	public TableEntry (int s, String p, String v, String a, int i) {
		state = s;
		predicate = p;
		value = v;
		attributes = a;
		id = i;
	}
	
	// Version with call stack
	public TableEntry (int s, String p, String v, int i, String a, String c) {
		if (LTSExtractor.fullDebugOn) {
			System.out.println(this.getClass().getName() + "("+s+","+p+","+v+","+i+","+a+","+c+")");
		}
		state = s;
		predicate = p;
		value = v;
		id = i;
		attributes = a;
		cs = c;
		
		//System.out.println ("table entry created!");
	}

	protected int getState () { return state; }
	protected String getPredicate () { return predicate; }
	protected String getValue () { return value; }
	protected int getId () { return id;	}
	protected String getAttributes () { return attributes; }
	protected String getCallStack () { return cs; }

	protected int size () {
		int entrySize = (4 * state) + (2 * predicate.length ()) + (2 * value.length ()) + (2 * attributes.length ()) + (4 * id) + (2 * cs.length ());
		return entrySize;
	}
}