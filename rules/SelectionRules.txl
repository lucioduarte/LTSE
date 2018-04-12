% Author: Lucio Mauro Duarte

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Includes trace information in if commands

% INPUT: 
% if (<cond>) <statements>
% OUTPUT: 
% {
%   if (<cond>) {
%     System.err.println ("SEL_ENTER:" + "(<cond>)" + "#" + true + "#" + NAME + "=" + OID + "#" + "{" + <attr_list> + "}" + "#" + <CID>);
%     <statements>
%   }
%   else {
%     System.err.println ("SEL_ENTER:" + "(<cond>)" + "#" + false + "#" + NAME + "=" + OID + "#" + "{" + <attr_list> + "}" + "#" + <CID>);
%   }
%   System.err.println ("SEL_END:(<cond>)" + "#" + NAME + "=" + OID + "#" + <CID>);
% }

rule traced_if
  replace [statement]
    RS [original_statement]
  deconstruct RS
    'if '( E [expression] ') S [statement]
    
  % Recovers current attribute list
  import attrib_list [printable_list]
  % Recovers current ID number
  import counter [number]
  
  import NAME [id]
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import SEL_ENTER [stringlit]
  import SEL_END [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]
  
  % Creates annotations
  construct EXP [stringlit]
  	_ [quote E]
  construct STR [printable_list]
    SEL_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP    
    
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB 
  
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]
    
 
  construct COMP_TRUE_MSG [printable_list]
    STR '+ 'true '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_TRUE_CMD [print_statement]
    System.err.println(COMP_TRUE_MSG);
     
  construct COMP_FALSE_MSG [printable_list]
    STR '+ 'false '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_FALSE_CMD [print_statement]
    System.err.println(COMP_FALSE_MSG);
    
  % Updates ID information
  export counter
    counter [+ 1]

  construct END_MSG [printable_list]
    SEL_END '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);

  % Marks statement as processed and includes annotations
  construct NES [else_clause]
  	'else '{ MSG_FALSE_CMD '}
  construct NIS [processed_statement]
  	'{ MSG_TRUE_CMD S '}
  
  construct NI [processed_statement]
    'if '( E ') NIS NES
  construct NS [processed_statement]
    '{
      NI
      END_MSG_CMD
    '}
  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Includes trace information in if commands with else clause

% INPUT: 
% if (<cond>) <stmts> else <stmts2>
% OUTPUT: 
% {
%   if (<cond>) {
%     System.err.println ("SEL_ENTER:" + "(<cond>)" + "#" + true + "#" + NAME + "=" + OID + "#" + "{" + <attr_list> + "}" + "#" + <CID>);
%     <stmts>
%   }
%   else {
%     System.err.println ("SEL_ENTER:" + "(<cond>)" + "#" + false + "#" + NAME + "=" + OID + "#" + "{" + <attr_list> + "}" + "#" + <CID>);
%     <stmts2>
%   }
%   System.err.println ("SEL_END:(<cond>)" + "#" + NAME + "=" + OID + "#" + <CID>);
% }

rule traced_if_else
  replace [statement]
    RS [original_statement]
  deconstruct RS
    'if '( E [expression] ') S1 [statement] C [else_clause]
  deconstruct C
  	'else S2 [statement]
  	
  % Recovers current attribute list
  import attrib_list [printable_list]
  % Recovers current ID number
  import counter [number]
  
  import NAME [id]
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import SEL_ENTER [stringlit]
  import SEL_END [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]
  
  % Creates annotations
  construct EXP [stringlit]
  	_ [quote E]      
    
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
    
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]    
        
  construct COMP_TRUE_MSG [printable_list]
    SEL_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ 'true '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_TRUE_CMD [print_statement]
    System.err.println(COMP_TRUE_MSG);
    
  construct COMP_FALSE_MSG [printable_list]
    SEL_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ 'false '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_FALSE_CMD [print_statement]
    System.err.println(COMP_FALSE_MSG);
    
  % Updates ID information
  export counter
    counter [+ 1]
   
  construct END_MSG [printable_list]
    SEL_END '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);

  % Marks statement as processed and includes annotations
  construct NES [else_clause]
  	'else '{ MSG_FALSE_CMD S2 '}
  construct NIS [processed_statement]
  	'{ MSG_TRUE_CMD S1 '}
  
  construct NI [processed_statement]
    'if '( E ') NIS NES
  construct NS [processed_statement]
    '{
      NI
      END_MSG_CMD
    '}
  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Includes trace information in switch commands

% INPUT: 
% switch (<exp>) { <alternatives> }
% OUTPUT: 
% INPUTS, sends it to rules delete_breaks, traced_case and traced_default

rule traced_switch
  replace [statement]
    RS [original_statement]
  deconstruct RS
    'switch '( E [expression] ') '{ A [repeat switch_alternative] '}

  % Exports condition to be used to annotate case and default clauses
  export cond [expression]
    E

  import counter [number]
  export sid [number]
    counter  
  % Updates ID information
  export counter
    counter [+ 1]

  % Marks statement as processed
  construct NS [processed_statement]
    'switch '( E ') '{ A '}
  construct NT [processed_statement]
    NS
  by
    NT [delete_breaks] [traced_case] [traced_default]
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Eliminates original break commands in the switch

% INPUT: 
% switch (<exp>) { case <val> : <statements> break; }
% OUTPUT: 
% switch (<exp>) { case <val> : <statements> }

rule delete_breaks
  replace [statement]
    RS [original_statement]
  deconstruct RS
    _ [break_statement]
by
  % empty
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Includes trace information in case clauses

% INPUT: 
% case <val> : <statements>
% OUTPUT: 
% case <val>: {
%               System.err.println("SEL_ENTER:(<val>)#" + <val> + "#" + NAME + "=" + OID + "#" + "{" + <attr_list> + "}" + "#" + <CID>);
%               {
%                 <statements>
%               }
%               System.err.println("SEL_END:(<val>)#" + "#" + NAME + "=" + OID + "#" + <CID>);
%               break;
%             }

rule traced_case
  replace $ [switch_alternative]
    RS [switch_alternative]
  deconstruct RS
    'case C [expression] ': D [repeat declaration_or_statement]

  % Imports condition of switch command
  import cond [expression]
  % Recovers current attribute list
  import attrib_list [printable_list]
  % Recovers current ID number
  import counter [number]
  
  import NAME [id]
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import SEL_ENTER [stringlit]
  import SEL_END [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]

  % Creates annotations
  construct STR [printable_list]
    
  construct EXP [stringlit]
    _ [quote C]
    
  % Includes ID information
  import sid [number]
  construct ID [stringlit]
    _ [quote sid]
    
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
  construct COMP_MSG [printable_list]
    SEL_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ C '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(COMP_MSG);
      
  construct END_MSG [printable_list]
    SEL_END '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);

  % Creates new break statement
  construct B [processed_statement]
    'break ';
  % Constructs new case clause with annotations
  construct ND [repeat declaration_or_statement]
    MSG_CMD
    '{
      D
    '}
    END_MSG_CMD
    B
  construct NA [switch_alternative]
    'case C ': '{
                  ND
               '}
  by
    NA
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Includes trace information in default clauses

% INPUT: 
% default : <statements>
% OUTPUT: 
% default: {
%               System.err.println("SEL_ENTER:(<val>)#" + <val> + "#" + NAME + "=" + OID + "#" + "{" + <attr_list> + "}" + "#" + <CID>);
%               {
%                 <statements>
%               }
%               System.err.println("SEL_END:(<val>)#" + "#" + NAME + "=" + OID + "#" + <CID>);
%               break;
%             }


rule traced_default
  replace $ [switch_alternative]
    RS [switch_alternative]
  deconstruct RS
    'default ': D [repeat declaration_or_statement]

  % Imports condition of switch command
  import cond [expression]
  % Recovers current attribute list
  import attrib_list [printable_list]
  % Recovers current ID number
  import sid [number]
  
  import NAME [id]
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import COLON [stringlit]
  import SEL_ENTER [stringlit]
  import SEL_END [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]

  % Creates annotations

  construct EXP [stringlit]
    _ [quote cond]
  construct COND [expression]
    cond
    
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
  % Includes ID information
  construct ID [stringlit]
    _ [quote sid]
    
  construct COMP_MSG [printable_list]
    SEL_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ COND '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(COMP_MSG);

  construct END_MSG [printable_list]
    SEL_END '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);
    
  export SW_EXP_MSG [print_statement]
    END_MSG_CMD

  % Constructs new default clause with annotations
  construct ND [repeat declaration_or_statement]
    MSG_CMD
    '{
      D
    '}
    END_MSG_CMD
  construct NA [switch_alternative]
    'default ': '{
                  ND
                '}
  by
    NA
end rule
