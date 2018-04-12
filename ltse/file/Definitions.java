package ltse.file;

/**
 * Basic definitions used in the tool.
 *
 * @author Lucio Mauro Duarte
 */

public interface Definitions {
	
	/** Determines current version */
	static final String VERSION = "06/01/2017";
	
	/* SPECIAL SEPARATORS */
	/** Symbol used as separator of information in annotations */
	static final String SEP = "#";
	/** Symbol used as separator of operation labels in annotations */
	static final String LABEL_SEP = ":";
	/** Symbol used as separator of class names in annotations */
	static final String CLASS_SEP = "=";
	/** Symbol used to indicate the end of an annotation */
	static final String END = ";";
	/** Symbol used to separate attribute information */
	static final String ATTR = "^";
	/** Symbol used to before a context ID */
	static final String CID_LABEL = "#";
	
	/* OPERATION LABELS */
	/** Label of the beginning of a selection command */
	static final String SEL_ENTER = "SEL_ENTER";
	/** Label of the end of a selection command */
	static final String SEL_END = "SEL_END";
	/** Label of the beginning of a repetition command */
	static final String REP_ENTER = "REP_ENTER";
	/** Label of the end of a selection command */
	static final String REP_END = "REP_END";
	/** Label of the beginning of an external method call */
	static final String CALL_ENTER = "CALL_ENTER";
	/** Label of the end of an external method call */
	static final String CALL_END = "CALL_END";
	/** Label of the beginning of an internal method call */
	static final String INT_CALL_ENTER = "INT_CALL_ENTER";
	/** Label of the end of an inernal method call */
	static final String INT_CALL_END = "INT_CALL_END";
	/** Label of the beginning of a method execution */
	static final String MET_ENTER = "MET_ENTER";
	/** Label of the end of a method execution */
	static final String MET_END = "MET_END";
	/** Label of the execution of an action */
	static final String ACTION = "ACTION";
	
	/* EXTENSIONS */
	/** Extension of log files */
	static final String LOG_EXT = "log";
	/** Extension of context files */
	static final String CTX_EXT = "ctx";
	/** Extension of LTS model files */
	static final String LTS_EXT = "lts";
	/** Extension of refinement files */
	static final String REF_EXT = "ref";
	/** Extension of filter files */
	static final String FLT_EXT = "flt";
	/** Extension of context table files */
	static final String CT_EXT = "ctb";
	/** Extension of saved model files */
	static final String MOD_EXT = "mdl";
	/** Extension of trace files */
	static final String TRC_EXT = "trc";
	/** Extension of occurrence table */
	static final String OCC_EXT = "occ";
	/** Extension of sequence table */
	static final String SEQ_EXT = "seq";
	/** Extension of probability table */
	static final String PROB_EXT = "prb";
	
	/* SPECIAL ACTIONS */
	/** Default representation of the empty action */
	static final String EMPTY_ACTION = "null";
	/** Default representation of the final action */
	static final String FINAL_ACTION = "_EXIT";
	/** Default suffix of method entering action */
	static final String ENTER_SUFFIX = ".enter";
	/** Default suffix of method exiting action */
	static final String EXIT_SUFFIX = ".exit";
	
	/* SPECIAL STATES */
	/** Default representation the final state */
	static final String FINAL_STATE = "FINAL";
	/** Default representation of the end state */
	//static final String END_STATE = "END";
	
	/* SPECIAL CONTEXT */
	/** Default representation of a global context. A global context
	 * determines a situation where execution is not occurring inside 
	 * a method. */
	static final String GLOBAL = "GLOBAL"; 
	/** Default context ID of global contexts */
	static final int GLOBAL_ID = -1;
	
	/* MODES */
	/** Code for call mode */
	static final int CALL = 0;
	/** Code for termination mode */
	static final int TERMINATION = 1;
	/** Code for entry and exit mode */
	static final int ENTRY_AND_EXIT = 2;
}