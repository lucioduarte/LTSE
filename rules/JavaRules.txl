% Author: Lucio Mauro Duarte
% Version: 27/06/2017

% TODO:
% - if (<method>(<parameters))...
% - ternary assignments
% - while (<method>([<parameters]))...
% - return (<method>([<parameters]));
% - <method1>(<method2>([<parameters>]))
% - method calls with more than one component name: C1.C2.m();
% - Non-static methods called inside a static method
% - return;
% - Not handled keywords: assert, enum, finally, goto, synchronized, transient, volatile, strictfp, native

include "bom.grm"
include "Java.Grm"
include "JavaCommentOverrides.Grm"
include "Definitions.txl"
include "AttributeRules.txl"
include "SelectionRules.txl"
include "RepetitionRules.txl"
include "MethodRules.txl"
include "ActionRules.txl"
include "ExceptionRules.txl"


% Escapes quotes inside strings
#pragma -esc "\"


%%%%% MAIN FUNCTION %%%%%
function main
  %%%%% Global variables initialisation %%%%%
  
  % Default separator character
  export SEP [stringlit]
  	  _[quote '#]
  	
  % Default annotation termination character
  export END [stringlit]
 	_[quote ';]
  
  % List of attributes
  export attrib_list [printable_list]
    ""
  % Attribute separator
  export ATTR [stringlit]
     _[quote '^]
    
  % Block ID counter
  export counter [number]
    0
  
  % Class name 
  export CLASS_NAME [id]
  	x
  	
  % Object ID
  export OID [printable_list]
  	x

  % Name of main method
  export MAIN [stringlit]
	_[quote 'main]

  % Return type void
  export VOID [stringlit]
       _[quote 'void]

  % Delimiters and markers
  export LCB [stringlit]
	_[quote '{]
  export RCB [stringlit]
 	_[quote '}]
  export LPAR [stringlit]
  	_[quote '(]
  export RPAR [stringlit]
  	_[quote ')]
  export COLON [stringlit]
   	_[quote ':]
  export DOT [stringlit]
       _[quote '.]
  
  % Context tags	
  export SEL_ENTER [stringlit]
  	_[quote 'SEL_ENTER]
  export SEL_END [stringlit]
   	_[quote 'SEL_END]
  export REP_ENTER [stringlit]
  	_[quote 'REP_ENTER]
  export REP_END [stringlit]
    	_[quote 'REP_END]
  export INT_CALL_ENTER [stringlit]
  	_[quote 'INT_CALL_ENTER]
  export INT_CALL_END [stringlit]
  	_[quote 'INT_CALL_END]
  export CALL_ENTER [stringlit]
  	_[quote 'CALL_ENTER]
  export CALL_END [stringlit]
  	_[quote 'CALL_END]
  export MET_ENTER [stringlit]
 	_[quote 'MET_ENTER]
  export MET_END [stringlit]
  	_[quote 'MET_END]
  export ACTION [stringlit]
    	_[quote 'ACTION]

  export IN [stringlit]
 	_[quote 'in]
  export IN_ACTION [printable_list]
       ACTION '+ COLON '+ IN '+ DOT
  export OUT [stringlit]
 	_[quote 'out]
  export OUT_ACTION [printable_list]
       ACTION '+ COLON '+ OUT '+ DOT

  % Applies rules to a given program, 
  % creating an instrumented version of it
  replace [program]
    P [program]
  by P
      %%%%% Class Rule %%%%%
  	  		
  	  [get_class_name]
  	  
      %%%%% Attribute Rules %%%%%
      
      [ignore_final_static_attribute]      
      % final static <type> <id> [<dimension>] [= <value>];

			[ignore_static_final_attribute]
      % static final <type> <id> [<dimension>] [= <value>];

			[ignore_final_attribute] 
      % final <type> <id> [<dimension>] [= <value>];
      
      [obtain_static_modifier_attribute]
      % static <modifier> <type> <id> [<dimension>] [= <value>];
      
      [obtain_modifier_static_attribute]
      % <modifier> static <type> <id> [<dimension>] [= <value>];

			[obtain_modifier_attribute]
			% <modifier> <type> <id> [<dimension>] [= <value>];
      
      [obtain_attribute]
      % <type> <id> [<dimension>] [= <value>];
      
      [obtain_user_attribute]
      % #attribute:<attr_name> = <expression>;
      
      %%%%% Class Rule %%%%%
  	  		
  	  [create_attributes]
      
      %%%%% Main method rule %%%%%
      
      %[traced_main_method]
      
      %%%%% Exception rules %%%%%
      
      [traced_throw_new_statement]
      % throws new E ();
      
      [traced_throw_statement]
      % throws E ();
      
      [traced_throw_statement_with_arguments]
      % throws E (<params>);
      
      [traced_try_catch]
      % try { <statements> } catch (E ()) { <statements>};
      
      %%%%% Method call rules %%%%%
      
      [traced_this_call]
      % this.<method> ([<arguments>])
      
      [traced_ext_met_call]
      % <object>.<method> ([<arguments>])
      
      [traced_ext_met_call_with_var_assignment]
      % <var> = <object>.<method> ([<arguments>])
      
      [traced_int_met_call]
      % <method> ([<arguments>])
      
      [traced_int_met_call_with_var_assignment]
      % <var> = <m> ([<arguments>])
      
      %%%%% User-defined action rules %%%%%
     	[traced_user_action]
      % #action:"<action_name>";
      
      [traced_user_input]
      % #input:<input_action>;
      
      [traced_user_output]
      % #output:"<output_action>";
      
      %%%%% Selection statement rules %%%%%
      [traced_if]
      % if (<cond>) <statements>
      
      [traced_if_else]
      % if (<cond>) <stmts> else <stmts2>
      
      [traced_switch]
      % switch (<exp>) { <alternatives> }
      
      %%%%% Repetition statement rules %%%%%
      
      [traced_true_while]
      % while (true) { <statements> }
      
      [traced_while]
      % while (<cond>) { <statements> }
      
      [traced_true_do_while]
      % do { <statements> } while (true);
      
      [traced_do_while]
      % do { <statements> } while (<cond>);
      
      [traced_true_for]
      % for (<ini>;true;<upd>) { <statements> }
      
      [traced_for]
      % for (<ini>;<cond>;<upd>) { <statements> }
      
      %%%%% Method body rules %%%%%
      
      [traced_static_method_with_return]
      % [<modifiers>] static [<modifiers>] <type> <method_name> ([<parameters>]) [<exceptions>] { <body> }
      
      [traced_method_with_return]
      % [<modfiers>] <type> <method_name> ([<parameters>]) [<exceptions>] { <body> }
      
      [traced_static_method]
      % [<modifiers>] static [<modifiers>] void <method_name> ([<parameters>]) [<exceptions>] { <body> }
      
      [traced_method]
      % [<modifiers>] void <method_name> ([<parameters>]) [<exceptions>] { <body> }

end function

rule get_class_name
	replace [class_header]
		RS [original_class_header]
		deconstruct RS
			M [repeat modifier] 'class C [class_name] E [opt extends_clause] I [opt implements_clause]
		deconstruct * C
			CNAME [id]
		import CLASS_NAME [id]
		export CLASS_NAME
			CNAME
		export CLASS_ID [printable_list]
			CLASS_NAME.class.getName()
		export OID [printable_list]
			this._thisInstanceID
		construct NS [processed_class_header]
 		RS
	by
		NS
end rule

rule create_attributes
	replace [class_or_interface_body]
		RS [original_class_or_interface_body]
		
		deconstruct RS
			'{                                    
       	 C [repeat class_body_declaration]    
    	'} 		
    
    % private static int _numberOfInstances = 0;
    %construct STATIC_NAME [id]
    %	_numberOfInstances	 
    %construct NSF [field_declaration]
    %	'private 'static 'int STATIC_NAME	'= 0 ';			
    	
    % private final int _thisInstanceID = this.hashCode ();
    construct	ID [id]
    	_thisInstanceID
    construct NIDF [field_declaration]
    	'private 'final 'int ID '= this.hashCode() '; 	
    	
    % private String _thisClassName = CLASS_NAME.class.getName();
    import CLASS_NAME [id]
    construct NAME [id]
    	_thisClassName
    export NAME
    construct CN [field_declaration]
    	'private 'final 'String NAME '= CLASS_NAME.class.getName() ';
    		
		construct NS [processed_class_or_interface_body]
		'{
			 %NSF
 			 NIDF
			 CN
			 C
 	'} 
	by
		NS
end rule
