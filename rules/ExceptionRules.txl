% Author: Lucio Mauro Duarte


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Traces try-catch structures

% INPUT: 
% try <block> <catch_clauses> [finally_clause]
% OUTPUT: 
% INPUT marked as processed and sends <catch_clauses>
% to specific rule

rule traced_try_catch
	replace [statement]
		RS [original_statement]

	deconstruct RS
		'try B [block] C [repeat catch_clause] F [opt finally_clause]

	construct NS [processed_statement]
		'try B C F

	by
		NS [traced_catch]
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Traces catch clauses

% INPUT: 
% catch (<type> <var>) <block>
% OUTPUT:
% catch (<type> <var>) 
% { 
%   System.err.println ("ACTION:" + (<var>.getClass ().getSimpleName ()).substring (0,1).toLowerCase () '+ 
%                                   (<var>.getClass ().getSimpleName ()).substring (1) + "#" + NAME + "=" + OID + "%");
%   <block>
% }

rule traced_catch
	replace $ [catch_clause]
		RS [catch_clause]

	deconstruct RS
		'catch '( T [type_specifier] V [id] ') B [block]
  	
  	import NAME [id]
  	import OID [printable_list]
  	import SEP [stringlit]
  	import COLON [stringlit]
  	import END [stringlit]
  	import ACTION [stringlit]

	% Creates annotation
	construct ENAME [printable_list]
		(V.getClass ().getSimpleName ()).substring (0,1).toLowerCase () '+ (V.getClass ().getSimpleName ()).substring (1)
		
	construct FINAL_MSG [printable_list]
		ACTION '+ COLON '+ ENAME '+ SEP '+ NAME '+ "=" '+ OID '+ END
		
	construct MSG_CMD [print_statement]
		System.err.println(FINAL_MSG);

	construct NB [block]
		'{
			MSG_CMD
			B
		'}

	construct NS [catch_clause]
		'catch '( T V ') NB

	by
		NS

end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Traces throw statements with new command

% INPUT: 
% throw new <exception class name> ([<arguments]);
% OUTPUT: 
% { 
%   System.err.println ("ACTION:" + <exception class name> + "#" + NAME + "=" + OID + "%");
%   INPUT
% } 

rule traced_throw_new_statement
	replace [statement]
		RS [original_statement]

	deconstruct RS
		throw E [expression] ';
		
	deconstruct E
		'new V [id] '( L [list argument] ')

  	import NAME [id]
  	import OID [printable_list]
  	import SEP [stringlit]
  	import COLON [stringlit]
  	import END [stringlit]
  	import ACTION [stringlit]
  	
  construct ENAME [printable_list]
		(V.class.getSimpleName ()).substring (0,1).toLowerCase () '+ (V.class.getSimpleName ()).substring (1)

	% Creates annotation
	construct FINAL_MSG [printable_list]
		ACTION '+ COLON '+ ENAME '+ SEP '+ NAME '+ "=" '+ OID '+ END
		
	construct MSG_CMD [print_statement]
		System.err.println(FINAL_MSG);
		
	construct PS [processed_statement]
		RS

	construct NB [processed_statement]
		'{
			MSG_CMD
			PS
		'}

	by
		NB
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Traces throw statements with arguments

% INPUT: 
% throw <exception class name> ([<arguments]);
% OUTPUT: 
% { 
%   System.err.println ("ACTION:" + <exception class name> + "#" + NAME + "=" + OID + "%");
%   INPUT
% } 

rule traced_throw_statement_with_arguments
	replace [statement]
		RS [original_statement]

	deconstruct RS
		throw E [expression] ';
		
	deconstruct E
		V [id] '( L [list argument] ')

  	import NAME [id]
  	import OID [printable_list]
  	import SEP [stringlit]
  	import COLON [stringlit]
  	import END [stringlit]
  	import ACTION [stringlit]
  	
  construct ENAME [printable_list]
		(V.getClass ().getSimpleName ()).substring (0,1).toLowerCase () '+ (V.getClass ().getSimpleName ()).substring (1)

	% Creates annotation
	construct FINAL_MSG [printable_list]
		ACTION '+ COLON '+ ENAME '+ SEP '+ NAME '+ "=" '+ OID '+ END
		
	construct MSG_CMD [print_statement]
		System.err.println(FINAL_MSG);
		
	construct PS [processed_statement]
		RS

	construct NB [processed_statement]
		'{
			MSG_CMD
			PS
		'}

	by
		NB
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Traces throw statements without arguments

% INPUT: 
% throw <exception class name>;
% OUTPUT: 
% { 
%   System.err.println ("ACTION:" + <exception class name> + "#" + NAME + "=" + OID + "%");
%   INPUT
% } 

rule traced_throw_statement
	replace [statement]
		RS [original_statement]

	deconstruct RS
		throw V [id] ';

  	import NAME [id]
  	import OID [printable_list]
  	import SEP [stringlit]
  	import COLON [stringlit]
  	import END [stringlit]
  	import ACTION [stringlit]
  	
  construct ENAME [printable_list]
		(V.getClass ().getSimpleName ()).substring (0,1).toLowerCase () '+ (V.getClass ().getSimpleName ()).substring (1)

	% Creates annotation
	construct FINAL_MSG [printable_list]
		ACTION '+ COLON '+ ENAME '+ SEP '+ NAME '+ "=" '+ OID '+ END
		
	construct MSG_CMD [print_statement]
		System.err.println(FINAL_MSG);
		
	construct PS [processed_statement]
		RS

	construct NB [processed_statement]
		'{
			MSG_CMD
			PS
		'}

	by
		NB
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
