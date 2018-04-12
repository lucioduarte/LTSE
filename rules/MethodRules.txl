% Author: Lucio Mauro Duarte
% Version: 13/12/2015

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%% INTERNAL METHOD CALLS %%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates internal method calls

% INPUT: 
% <m> ([<arguments>])
% OUTPUT: 
% {
%   System.err.println ("INT_CALL_ENTER:<m>" + "#" + NAME + "=" + OID + "#" + "{" + "" + "}" + "#" + <CID>);
%   INPUT
%   System.err.println ("INT_CALL_END:<m>" + "#" + NAME + "=" + OID + "#" + <CID>);
% }
rule traced_int_met_call
  replace [statement]
    RS [original_statement]
  deconstruct RS
    ES [expression_statement]
  deconstruct * [reference] ES
    MET [id] '( A [list  argument] ')
  
  % Recovers information about values of attributes
  import attrib_list [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import INT_CALL_ENTER [stringlit]
  import INT_CALL_END [stringlit]
  
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB  
  % Recovers ID information
  import counter [number]
  construct ID [stringlit]
    _ [quote counter]
  % Updates ID information
  export counter
    counter [+ 1]
    
  import NAME [id]
  import OID [printable_list]
  
  % Creates annotations
  construct MET_NAME [stringlit]
    _ [quote MET]  
  construct BEGIN_MET_MSG [printable_list]
    INT_CALL_ENTER '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct ENTER_MSG [print_statement]
    System.err.println(BEGIN_MET_MSG);

  construct END_MET_MSG [printable_list]
    INT_CALL_END '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct EXIT_MSG [print_statement]
    System.err.println(END_MET_MSG);
  
  % Marks statement as processed and includes annotations
  construct NM [processed_statement]
    RS
  construct NS [processed_statement]
    '{
      ENTER_MSG
      NM
      EXIT_MSG
    '}
  by
    NS 
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates method calls using 'this'

% INPUT: 
% this.<method> ([<arguments>]);
% OUTPUT: 
% INPUT marked as processed

rule traced_this_call
  replace [statement]
    RS [original_statement]
  deconstruct RS
    ES [expression_statement]
  deconstruct ES
    'this '. MET [id] '( A [list argument] ') ';

  % Recovers information about values of attributes
  import attrib_list [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import INT_CALL_ENTER [stringlit]
  import INT_CALL_END [stringlit]
  
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB  
  % Recovers ID information
  import counter [number]
  construct ID [stringlit]
    _ [quote counter]
  % Updates ID information
  export counter
    counter [+ 1]
    
  import NAME [id]
  import OID [printable_list]
  
  % Creates annotations
  construct MET_NAME [stringlit]
    _ [quote MET]  
  construct BEGIN_MET_MSG [printable_list]
    INT_CALL_ENTER '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct ENTER_MSG [print_statement]
    System.err.println(BEGIN_MET_MSG);

  construct END_MET_MSG [printable_list]
    INT_CALL_END '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct EXIT_MSG [print_statement]
    System.err.println(END_MET_MSG);
  
  % Marks statement as processed and includes annotations
  construct NM [processed_statement]
    RS
  construct NS [processed_statement]
    '{
      ENTER_MSG
      NM
      EXIT_MSG
    '}
  by
    NS 
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates internal method call as part of a variable declaration

% INPUT: 
% <var> = <m> ([<arguments>])
% OUTPUT: 
% {
%   System.err.println ("INT_CALL_ENTER:<m>" + "#" + NAME + "=" + OID + "#" + "{" + "" + "}" + "#" + <CID>);
%   INPUT
%   System.err.println ("INT_CALL_END:<m>" + "#" + NAME + "=" + OID + "#" + <CID>);
% }

rule traced_int_met_call_with_var_assignment
  replace [declaration_or_statement]
    RS [original_declaration]
  deconstruct RS
    LV [local_variable_declaration]
  deconstruct * [variable_declarator] LV
    VAR [id] = MET [id] '( A [list argument] ')
    
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import INT_CALL_ENTER [stringlit]
  import INT_CALL_END [stringlit]
  
  % Recovers information about values of attributes
  import attrib_list [printable_list]
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
    
  % Recovers ID information
  import counter [number]
  construct ID [stringlit]
    _ [quote counter]
  % Updates ID information
  export counter
    counter [+ 1]
  
  import NAME [id]
  import OID [printable_list]
  
  % Creates annotations
  construct MET_NAME [stringlit]
    _ [quote MET]
  construct BEGIN_MET_MSG [printable_list]
    INT_CALL_ENTER '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct ENTER_MSG [print_statement]
    System.err.println(BEGIN_MET_MSG);

  construct END_MET_MSG [printable_list]
    INT_CALL_END '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct EXIT_MSG [print_statement]
    System.err.println(END_MET_MSG);
  
  % Marks statement as processed and includes annotations
  construct NM [processed_declaration]
    RS
  construct NS [processed_statement]
    '{
      ENTER_MSG
      NM
      EXIT_MSG
    '}
  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%% EXTERNAL METHOD CALLS %%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates external method calls

% INPUT: 
% <c>.<m> ([<arguments>])
% OUTPUT: 
% {
%   System.err.println ("CALL_ENTER:<m>" + "#" + NAME + "=" + OID + "#" + <c> + "#" + "{" + "" + "}" + "#" + <CID>);%   <OLD> System.err.println ("ACTION:<m>" + "#" + NAME + "=" + OID);
%   INPUT
%   System.err.println ("CALL_END:<m>" + "#" + NAME + "=" + OID + "#" + <c> + "#" + <CID>);%   <OLD> System.err.println ("ACTION:<m>" + "#" + NAME + "=" + OID);
% }
rule traced_ext_met_call
  replace [statement]
    RS [original_statement]
  deconstruct RS
    ES [expression_statement]
  deconstruct * [reference] ES
    COMP [id] '. MET [id] '( A [list  argument] ')
  
  % Recovers information about values of attributes
  import attrib_list [printable_list]
  import NAME [id]
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import CALL_ENTER [stringlit]
  import CALL_END [stringlit]
  % Recovers ID information
  import counter [number]
  
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
    
  construct ID [stringlit]
    _ [quote counter]
  % Updates ID information
  export counter
    counter [+ 1]

  construct COMP_NAME [stringlit]
    _ [quote COMP]
  
  % Creates annotations
  construct MET_NAME [stringlit]
    _ [quote MET]
  construct BEGIN_MET_MSG [printable_list]
    CALL_ENTER '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ COMP_NAME '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct ENTER_MSG [print_statement]
    System.err.println(BEGIN_MET_MSG);

  construct END_MET_MSG [printable_list]
    CALL_END '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ COMP_NAME '+ SEP '+ ID '+ END
  construct EXIT_MSG [print_statement]
    System.err.println(END_MET_MSG);
  
  % Marks statement as processed and includes annotations
  construct NM [processed_statement]
    RS
  construct NS [processed_statement]
    '{
      ENTER_MSG
      NM
      EXIT_MSG
    '}
  by
    NS 
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates external method call as part of a variable declaration

% INPUT: 
% <var> = <c>.<m> ([<arguments>])
% OUTPUT: 
% {
%   System.err.println ("CALL_ENTER:<m>" + "#" + NAME + "=" + OID + "#" + <c> + "#" + "{" + "" + "}" + "#" + <CID>);
%   INPUT
%   System.err.println ("CALL_END:<m>" + "#" + NAME + "=" + OID + "#" + <c> + "#" + <CID>);
% }

rule traced_ext_met_call_with_var_assignment
  replace [declaration_or_statement]
    RS [original_declaration]
  deconstruct RS
    LV [local_variable_declaration]
  deconstruct * [variable_declarator] LV
    VAR [id] = COMP [id] '. MET [id] '( A [list argument] ')
    
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import CALL_ENTER [stringlit]
  import CALL_END [stringlit]
  
  % Recovers information about values of attributes
  import attrib_list [printable_list]
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
    
  % Recovers ID information
  import counter [number]
  construct ID [stringlit]
    _ [quote counter]
  % Updates ID information
  export counter
    counter [+ 1]
  
  import NAME [id]
  import OID [printable_list]
  
  construct COMP_NAME [stringlit]
    _ [quote COMP]
  
  % Creates annotations
  construct MET_NAME [stringlit]
    _ [quote MET]
  construct BEGIN_MET_MSG [printable_list]
    CALL_ENTER '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ COMP_NAME '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct ENTER_MSG [print_statement]
    System.err.println(BEGIN_MET_MSG);

  construct END_MET_MSG [printable_list]
    CALL_END '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ COMP_NAME '+ SEP '+ ID '+ END
  construct EXIT_MSG [print_statement]
    System.err.println(END_MET_MSG);
  
  % Marks statement as processed and includes annotations
  construct NM [processed_declaration]
    RS
  construct NS [processed_statement]
    '{
      ENTER_MSG
      NM
      EXIT_MSG
    '}
  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%% NON-STATIC METHOD BODIES %%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates non-static method bodies with no return

% INPUT: 
% [<modifiers>] <type> <method_name> ([<parameters>]) [<exceptions>] { <body> }
% OUTPUT: 
% INPUT, passing <body> to rule methodAnnotation

rule traced_method
  replace [method_declaration]
    RS [original_method_declaration]
  deconstruct RS
    M [repeat modifier] T [type_specifier] Decl [method_declarator] 
    E [opt throws] Body [method_body]
  deconstruct Decl
    MET [method_name] '( P [list formal_parameter] ')
    
  construct MAIN [stringlit]
		_[quote 'main]

  % Avoids annotating main method
  construct MET_NAME [stringlit]
  		_ [quote MET]
  		
  where not
    MET_NAME [= MAIN]
   
  % Checks whether the method has no return
  construct TYPE_NAME [stringlit]
    _ [quote T]
  construct VOID [stringlit]
		_[quote 'void]
  where 
    TYPE_NAME [= VOID]
   
  % Saves the method's name 
  export MET

  construct ND [processed_method_declaration]
    RS
  by
    ND [methodAnnotation]
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Includes messages inside non-static method bodies with no return

% INPUT: 
% { <repeat declaration_or_statement> }
% OUTPUT: 
% {
%   System.err.println ("MET_ENTER:<method_name>" + "#" + NAME + "=" + OID + "#" + "{" + "<attribs>" + "}" + "#" + <CID>);
%   {
%      <repeat declaration_or_statement>
%   } System.err.println ("MET_END:<method_name>" + "#" + NAME + "=" + OID + "#" + <CID>);

% }

rule methodAnnotation
  replace [method_body]
    RS [original_method_body]
  deconstruct RS
  '{ D [repeat declaration_or_statement] '}
  
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import MET_ENTER [stringlit]
  import MET_END [stringlit]

  % Recovers the method's name
  import MET [method_name]
  % Recovers ID information
  import counter [number]
  % Recovers the current attribute list
  import attrib_list [printable_list]
    
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
    
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]
  % Updates ID information
  export counter
    counter [+ 1]
    
  import NAME [id]
  import OID [printable_list]

  % Creates annotations
  construct MET_NAME [stringlit]
    _ [quote MET]   
  construct BEGIN_MET_MSG [printable_list]
    MET_ENTER '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct ENTER_MSG [print_statement]
    System.err.println(BEGIN_MET_MSG);

  construct END_MET_MSG [printable_list]
    MET_END '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct EXIT_MSG [print_statement]
    System.err.println(END_MET_MSG);

  % Includes annotation in method body
  construct NewBody [processed_method_body]
    '{
      ENTER_MSG
      '{
        D
      '} 
      EXIT_MSG
    '}
  by
    NewBody
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates non-static methods with return statement

% INPUT: 
% [<modfiers>] <type> <method_name> ([<parameters>]) [<exceptions>] { <body> }
% OUTPUT: 
% INPUT, passing <body> to rules methodAnnotation2 and traced_return

rule traced_method_with_return
  replace [method_declaration]
    RS [original_method_declaration]
  deconstruct RS
    M [repeat modifier] T [type_specifier] Decl [method_declarator] 
    E [opt throws] Body [method_body]

  deconstruct Decl
    MET [method_name] '( P [list formal_parameter] ')

	construct MAIN [stringlit]
  		_ [quote 'main]

  % Avoids annotating main method
  construct MET_NAME [stringlit]
    _ [quote MET]
    
  where not
    MET_NAME [= MAIN]
  export MET_NAME
  
  construct VOID [stringlit]
  		_ [quote 'void]
  
  % Checks that the methos has a return type
  construct TYPE_NAME [stringlit]
    _ [quote T]
    
  where not
    TYPE_NAME [= VOID]

  construct ND [processed_method_declaration]
    RS
  by
    ND [methodAnnotation2] [traced_return] 
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Includes messages in non-static method bodies with return statement

% INPUT: 
% { <repeat declaration_or_statement> }
% OUTPUT: 
% {
%   System.err.println ("MET_ENTER:<method_name>" + "#" + NAME + "=" + OID + "#" + "{" + <attr_list> + "}" + "#" + <CID>);
%   {
%      <repeat declaration_or_statement>
%   } 
% }

rule methodAnnotation2
  replace [method_body]
    RS [original_method_body]
  deconstruct RS
    '{ D [repeat declaration_or_statement] '}

  % Recovers the method's name
  import MET_NAME [stringlit]
  % Recovers ID information
  import counter [number]
  % Recovers the current attribute list
  import attrib_list [printable_list]
  
  import NAME [id]
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import MET_ENTER [stringlit]

  % Creates annotation
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]
    
  export RID [number]
  	counter
  	
  export counter
  	counter [+ 1]
   
  construct BEGIN_MET_MSG [printable_list]
    MET_ENTER '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct ENTER_MSG [print_statement]
    System.err.println(BEGIN_MET_MSG);
 

  % Includes annotation in method body
  construct NewBody [processed_method_body]
    '{
      ENTER_MSG
      '{
        D
      '}
    '}
  by
    NewBody
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates return statement inside a non-static method body

% INPUT: 
% return <exp>;
% OUTPUT: 
% {
%  System.err.println ("MET_END:<method_name>" + "#" + NAME + "=" + OID + "#" + <CID>);  <OLD> System.err.println ("ACTION:<method_name>" + "#" + NAME + "=" + OID);
%  INPUT
% }

rule traced_return
  replace [return_statement]
    RS [original_return]    
  deconstruct RS
    'return RV [expression] ';
    
  import SEP [stringlit]
  import END [stringlit]
  import COLON [stringlit]
  import MET_END [stringlit]
  import RCB [stringlit]
  import LCB [stringlit]
  % Recovers the current attribute list
  import attrib_list [printable_list]

  % Recovers the method's name
  import MET_NAME [stringlit]
  % Recovers ID information
  import RID [number]
  
  import NAME [id]
  import OID [printable_list]
  
  % Includes ID information
  construct ID [stringlit]
    _ [quote RID]

	% Creates annotation
 % construct ATTR_MSG [printable_list]
  %  LCB '+ attrib_list '+ RCB

  construct END_MET_MSG [printable_list]
    MET_END '+ COLON '+ MET_NAME '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct EXIT_MSG [print_statement]
    System.err.println(END_MET_MSG);
  
  construct PR [processed_return]
    RS
  
  construct NR [processed_return]
    '{ 
      EXIT_MSG 
      PR
    '}
  by
    NR
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%% STATIC METHOD BODIES %%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates static method bodies with no return

% INPUT: 
% [<modifiers>] 'static [<modifiers] void <method_name> ([<parameters>]) [<exceptions>] { <body> }
% OUTPUT: 
% INPUT, passing <body> to rule methodAnnotation

rule traced_static_method
  replace [method_declaration]
    RS [original_method_declaration]
  deconstruct RS
    M [repeat modifier] T [type_specifier] Decl [method_declarator] 
    E [opt throws] Body [method_body]
  deconstruct Decl
    MET [method_name] '( P [list formal_parameter] ')
  deconstruct * [modifier] M
  	'static
    
  construct MAIN [stringlit]
		_[quote 'main]

  % Avoids annotating main method
  construct MET_NAME [stringlit]
  		_ [quote MET]
  		
  where not
    MET_NAME [= MAIN]
  
   
  % Checks whether the method has no return
  construct TYPE_NAME [stringlit]
    _ [quote T]
  construct VOID [stringlit]
		_[quote 'void]
  where 
    TYPE_NAME [= VOID]
   
  % Saves the method's name 
  export MET

  construct ND [processed_method_declaration]
    RS
  by
    ND [staticMethodAnnotation]
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Includes messages inside static method bodies with no return

% INPUT: 
% { <repeat declaration_or_statement> }
% OUTPUT: 
% {
%   System.err.println ("MET_ENTER:<method_name>" + "#" + CLASS_ID + "=" + CLASS_ID + "#" + "{" + "<attribs>" + "}" + "#" + <CID>);
%   {
%      <repeat declaration_or_statement>
%   } System.err.println ("MET_END:<method_name>" + "#" + CLASS_ID + "=" + CLASS_ID + "#" + <CID>);

% }

rule staticMethodAnnotation
  replace [method_body]
    RS [original_method_body]
  deconstruct RS
  '{ D [repeat declaration_or_statement] '}
  
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import MET_ENTER [stringlit]
  import MET_END [stringlit]

  % Recovers the method's name
  import MET [method_name]
  % Recovers ID information
  import counter [number]
  % Recovers the current attribute list
  import attrib_list [printable_list]
    
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
    
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]
  % Updates ID information
  export counter
    counter [+ 1]
    
  import NAME [id]
  import CLASS_ID [printable_list]

  % Creates annotations
  construct MET_NAME [stringlit]
    _ [quote MET]   
  construct BEGIN_MET_MSG [printable_list]
    MET_ENTER '+ COLON '+ MET_NAME '+ SEP '+ CLASS_ID '+ "=" '+ CLASS_ID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct ENTER_MSG [print_statement]
    System.err.println(BEGIN_MET_MSG);

  construct END_MET_MSG [printable_list]
    MET_END '+ COLON '+ MET_NAME '+ SEP '+ CLASS_ID '+ "=" '+ CLASS_ID '+ SEP '+ ID '+ END
  construct EXIT_MSG [print_statement]
    System.err.println(END_MET_MSG);

  % Includes annotation in method body
  construct NewBody [processed_method_body]
    '{
      ENTER_MSG
      '{
        D
      '} 
      EXIT_MSG
    '}
  by
    NewBody
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates static method bodies with return

% INPUT: 
% [<modifiers>] 'static [<modifiers] <type> <method_name> ([<parameters>]) [<exceptions>] { <body> }
% OUTPUT: 
% INPUT, passing <body> to rule methodAnnotation

rule traced_static_method_with_return
  replace [method_declaration]
    RS [original_method_declaration]
  deconstruct RS
    M [repeat modifier] T [type_specifier] Decl [method_declarator] 
    E [opt throws] Body [method_body]
  deconstruct Decl
    MET [method_name] '( P [list formal_parameter] ')
  deconstruct * [modifier] M
  	'static
    
  construct MAIN [stringlit]
		_[quote 'main]

  % Avoids annotating main method
  construct MET_NAME [stringlit]
  		_ [quote MET]
  		
  where not
    MET_NAME [= MAIN]
    
  export MET_NAME
  
   
  % Checks whether the method has return
  construct TYPE_NAME [stringlit]
    _ [quote T]
  construct VOID [stringlit]
		_[quote 'void]
  where not
    TYPE_NAME [= VOID]
   
  % Saves the method's name 
  export MET

  construct ND [processed_method_declaration]
    RS
  by
    ND [staticMethodAnnotation2] [traced_static_return]
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Includes messages in static method bodies with return statement

% INPUT: 
% { <repeat declaration_or_statement> }
% OUTPUT: 
% {
%   System.err.println ("MET_ENTER:<method_name>" + "#" + CLASS_ID + "=" + CLASS_ID + "#" + "{" + <attr_list> + "}" + "#" + <CID>);
%   {
%      <repeat declaration_or_statement>
%   } 
% }

rule staticMethodAnnotation2
  replace [method_body]
    RS [original_method_body]
  deconstruct RS
    '{ D [repeat declaration_or_statement] '}

  % Recovers the method's name
  import MET_NAME [stringlit]
  % Recovers ID information
  import counter [number]
  % Recovers the current attribute list
  import attrib_list [printable_list]
  
  import CLASS_ID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import MET_ENTER [stringlit]

  % Creates annotation
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]
    
  export RID [number]
  	counter
  	
  export counter
  	counter [+ 1]
   
  construct BEGIN_MET_MSG [printable_list]
    MET_ENTER '+ COLON '+ MET_NAME '+ SEP '+ CLASS_ID '+ "=" '+ CLASS_ID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct ENTER_MSG [print_statement]
    System.err.println(BEGIN_MET_MSG);
 

  % Includes annotation in method body
  construct NewBody [processed_method_body]
    '{
      ENTER_MSG
      '{
        D
      '}
    '}
  by
    NewBody
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Annotates return statement inside a static method body

% INPUT: 
% return <exp>;
% OUTPUT: 
% {
%  System.err.println ("MET_END:<method_name>" + "#" + NAME + "=" + OID + "#" + <CID>);  <OLD> System.err.println ("ACTION:<method_name>" + "#" + NAME + "=" + OID);
%  INPUT
% }

rule traced_static_return
  replace [return_statement]
    RS [original_return]    
  deconstruct RS
    'return RV [expression] ';
    
  import SEP [stringlit]
  import END [stringlit]
  import COLON [stringlit]
  import MET_END [stringlit]
  import RCB [stringlit]
  import LCB [stringlit]
  % Recovers the current attribute list
  import attrib_list [printable_list]

  % Recovers the method's name
  import MET_NAME [stringlit]
  % Recovers ID information
  import RID [number]
  
 	import CLASS_ID [printable_list]
  
  % Includes ID information
  construct ID [stringlit]
    _ [quote RID]

	% Creates annotation
 % construct ATTR_MSG [printable_list]
  %  LCB '+ attrib_list '+ RCB

  construct END_MET_MSG [printable_list]
    MET_END '+ COLON '+ MET_NAME '+ SEP '+ CLASS_ID '+ "=" '+ CLASS_ID '+ SEP '+ ID '+ END
  construct EXIT_MSG [print_statement]
    System.err.println(END_MET_MSG);
  
  construct PR [processed_return]
    RS
  
  construct NR [processed_return]
    '{ 
      EXIT_MSG 
      PR
    '}
  by
    NR
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%% SPECIAL CASES %%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%

% - if (<method>(<parameters)) { <declarations_or_statements> }
% - while (<method>([<parameters])) { <declarations_or_statements> }
% - return (<method>([<parameters]));
% - <method1>(<method2>([<parameters>]))
% - method calls with more than one component name: C1.C2.m();
% - [<type>] <var> = <method>([<parameters>] <operattion> <method>([<parameters>];

