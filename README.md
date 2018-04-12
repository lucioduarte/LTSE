LTSE
====

The Labelled Transition Systems Extractor tool project. The tool extracts LTS models from Java code based on execution traces produced by annotated source code. More information can be found in the following paper about the approach and the tool:

https://link.springer.com/article/10.1007%2Fs10270-015-0466-0

Requirements:
1. Java
2. TXL (optional)

Settings:
1. set environment variable LTSE: path to ltse.jar
2. set environment variable INSTR_PATH (TXL rules - optional): path to 'rules' folder

Running:
1. If using TXL

  txl -i "$INSTR_PATH/rules" -in 2 -o \<outputFile\>.java \<inputFile\>.java $INSTR_PATH/rules/JavaRules.txl

2. Run

  java -jar ltse.jar [\<filterFile\>] [\<refinementFile\>] [\<actionMode\>] \<list-of-logs\> [\<specFile\>]
  

where:
 - \<\<filterFile\>: Name of ".flt" file containing the names of actions to be included in the model (one name per line) - OPTIONAL, default is all actions included
 - \<refinementFile\>: Name of ".ref" file containing the names of attributes to be included in context information (one name per line) - OPTIONAL, default is no attributes
 - \<actionMode\>: Determines how actions related to method calls/execution are represented and interpreted (OPTIONAL, default is call mode):
    -c (call mode): action name "m" represents the call of the corresponding method m (i.e., before its execution);
    -t (termination mode): action name "m" represents the termination of the corresponding method m (i.e., after its execution);
    -e (enter/exit mode): action name "m.enter" represents the beginning of the execution of corresponding method m, whereas "m.exit" represents the end of the execution;
 - \<list-of-logs\>: List of names of ".log" files containing each one execution trace with context information
 - \<specFile\>: Specification to be included in the model file in one of the formats allowed by the LTSA tool (automaton property or LTL/FLTL formula) 
  
  It is recommended that the outputs of the LTSE tool (sent to the standard error output) be redirected to a result file.
  Correct execution should produce 3 files:
  
- A ".mdl" file containing the textual description of the generated model;
- A ".ctb" file containing a description of the context table created during the process;
- A ".lts" file containing the LTS model in the format input of the LTSA tool


