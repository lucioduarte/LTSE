% Author: Lucio Mauro Duarte


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% WHILE TRUE STATEMENTS

% INPUT: 
% while (true) { <statements> }
%
% OUTPUT: 
% while (true)
%    {
%      System.err.println ("REP_ENTER:(true)#" + true + "#" + <class name> + "=" + <object ID> + "#" + "{" + <Attributes> + "}" + "#" + <PC> + ";");
%      {
%        <statements>
%      } System.err.println ("REP_END:(true)" + "#" + <class name> + "=" + <object ID> + "#" + <PC> + ";");
%    }

rule traced_true_while
  replace [statement]
    RS [original_statement]
    
  deconstruct RS
    'while '( E [expression] ') S [statement]
  deconstruct E
    'true
  
  % Current attribute list
  import attrib_list [printable_list]
  % Current PC number
  import counter [number]
  % Class ID
  import NAME [id]
  % Object ID
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]
  import COLON [stringlit]
  import REP_ENTER [stringlit]
  import REP_END [stringlit]
  
  % Statement label
  construct EXP [stringlit]
    _ [quote E]
     
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]
    
  construct COMP_MSG [printable_list]
    REP_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ 'true '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(COMP_MSG);
    
  % Updates ID information
  export counter
    counter [+ 1]
    
  construct END_MSG [printable_list]
    REP_END '+ COLON '+ LPAR '+ EXP '+ RPAR  '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);

  % Marks statement as processed and includes annotations
  construct NB [processed_statement]
    '{
      MSG_CMD
      S
      END_MSG_CMD
    '}
   
  % Generates new annotated statement 
  construct NW [processed_statement]
    'while '( E ') NB

  by
    NW
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%  WHILE STATEMENTS

% INPUT: 
% while (<cond>) { <statements> }
%
% OUTPUT: 
% {
%	while (<cond>)
%   {
%	   System.err.println ("REP_ENTER:(<cond>)#" + true + "#" + <class name> + "=" + <object ID> + "#" + "{" + <Attributes> + "}" + "#" + <PC> + ";");
%      {
%		 <statements>
%      } System.err.println ("REP_END:(<cond>)" + "#" + <class name> + "=" + <object ID> + "#" + <PC> + ";");
%   }
%   System.err.println ("REP_ENTER:(<cond>)#" + false + "#" + <class name> + "=" + <object ID> + "#" + "{" + <Attributes> + "}" + "#" + <PC> + ";");
%   System.err.println ("REP_END:(<cond>)" + "#" + <class name> + "=" + <object ID> + "#" + <PC> + ";");
% }

rule traced_while
  replace [statement]
    RS [original_statement]
    
  deconstruct RS
    'while '( E [expression] ') S [statement]
    
  % Current attribute list
  import attrib_list [printable_list]
  % Current PC number
  import counter [number]
  % Class name
  import NAME [id]
  % Object ID
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]
  import COLON [stringlit]
  import REP_ENTER [stringlit]
  import REP_END [stringlit]

  % Creates annotations
  construct EXP [stringlit]
    _ [quote E]
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB    
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]
    
  % Updates ID information
  export counter
    counter [+ 1]  

  construct COMP_MSG [printable_list]
    REP_ENTER '+  COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ 'true '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(COMP_MSG);
    
  construct COMP_NOT_MSG [printable_list]
    REP_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ 'false '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct NOT_MSG_CMD [print_statement]
    System.err.println(COMP_NOT_MSG);
    
  construct END_MSG [printable_list]
    REP_END '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END  
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);

  % Marks statement as processed and includes annotations
  construct NB [processed_statement]
    '{
      MSG_CMD
      S
      END_MSG_CMD
    '}
  
  % Creates new while statement  
  construct NW [processed_statement]
    'while '( E ') NB

  % Constructs new annotated while statement
  construct NS [processed_statement]
    '{
      NW
      NOT_MSG_CMD
      END_MSG_CMD
    '}

  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% DO-WHILE TRUE STATEMENTS (converted to a WHILE TRUE)

% INPUT: 
% do { <statements> } while (true);
%
% OUTPUT: 
% {
%  {
%    <statements>
%  } while (true)
%    {
%      System.err.println ("REP_ENTER:(true)#" + true + "#" + WhileTest.class.getName () + "=" + hashCode () + "#" + "{" + "" + "}" + "#" + "0" + ";");
%        {
%          <statements>
%        } System.err.println ("REP_END:(true)" + "#" + WhileTest.class.getName () + "=" + hashCode () + "#" + "0" + ";");
%    }
%
%    System.err.println ("REP_ENTER:(true)#" + false + "#" + WhileTest.class.getName () + "=" + hashCode () + "#" + "{" + "" + "}" + "#" + "0" + ";");
%    System.err.println ("REP_END:(true)" + "#" + WhileTest.class.getName () + "=" + hashCode () + "#" + "0" + ";");
% }


rule traced_true_do_while
  replace [statement]
    RS [original_statement]
    
  deconstruct RS
    'do S [statement] 'while '( E  [expression] ') ';
  deconstruct E
    'true
    
  % Current attribute list
  import attrib_list [printable_list]
  % Current PC number
  import counter [number]
  % Class name
  import NAME [id]
  % Object ID
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]
  import COLON [stringlit]
  import REP_ENTER [stringlit]
  import REP_END [stringlit]
  
  % Statement label
  construct EXP [stringlit]
    _ [quote E]

  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
    
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]
    
  % Updates ID information
  export counter
    counter [+ 1]  
    
  construct COMP_MSG [printable_list]
    REP_ENTER '+ COLON '+ LPAR  '+ EXP '+ RPAR '+ SEP '+ 'true '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(COMP_MSG);
    
  construct END_MSG [printable_list]
    REP_END '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);

  % Marks statement as processed and includes annotations
  construct NB [processed_statement]
    '{
      MSG_CMD
      S
      END_MSG_CMD
    '}
  
  % Creates new while statement  
  construct NW [processed_statement]
    'while '( E ') NB

  % Constructs new annotated while statement
  construct NS [processed_statement]
    '{
      S
      NW
    '}

  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% DO-WHILE STATEMENTS (converted to WHILE STATEMENTS)

% INPUT: 
% do { <statements> } while (<cond>);
%
% OUTPUT: 
% {
%   {
%     <statements>
%   } while (<cond>)
%     {
%       System.err.println ("REP_ENTER:(<cond>)#" + true + "#" + <class name> + "=" + <object ID> + "#" + "{" + <attributes> + "}" + "#" + <PC> + ";");
%       {
%         <statements>
%       } System.err.println ("REP_END:(<cond>)" + "#" + <class name> + "=" + <object ID> + "#" + <PC> + ";");
%     }
%
%     System.err.println ("REP_ENTER:(<cond>)#" + false + "#" + <class name> + "=" + <object ID> + "#" + "{" + <attributes> + "}" + "#" + <PC> + ";");
%     System.err.println ("REP_END:(<cond>)" + "#" + <class name> + "=" + <object ID> + "#" + <PC> + ";");
% }


% Includes trace information in do-statements
rule traced_do_while
  replace [statement]
    RS [original_statement]
    
  deconstruct RS
    'do S [statement] 'while '( E  [expression] ') ';
    
  % Current attribute list
  import attrib_list [printable_list]
  % Current PC number
  import counter [number]
  % Class name
  import NAME [id]
  % Object ID
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]
  import COLON [stringlit]
  import REP_ENTER [stringlit]
  import REP_END [stringlit]

  % Creates annotations
  construct EXP [stringlit]
    _ [quote E]
    
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
  construct ID [stringlit]
    _ [quote counter]
    
  construct COMP_MSG [printable_list]
    REP_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ 'true '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(COMP_MSG);
       
  construct COMP_NOT_MSG [printable_list]
    REP_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ 'false '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct NOT_MSG_CMD [print_statement]
    System.err.println(COMP_NOT_MSG);
 
  % Updates ID information
  export counter
    counter [+ 1]

  construct END_MSG [printable_list]
    REP_END '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);

  % Marks statement as processed and includes annotations
  construct NB [processed_statement]
    '{
      MSG_CMD
      S
      END_MSG_CMD
    '}
    
  construct NW [processed_statement]
    'while '( E ') NB
    
  construct NS [processed_statement]
    '{
      S
      NW
      NOT_MSG_CMD
      END_MSG_CMD
    '}
  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%  FOR-TRUE STATEMENTS

% INPUT: 
% for ([<ini>];;[<upd>]) { <statements> }
%
% OUTPUT: 
% for ([<ini>];;[<upd>])
% {
%    System.err.println ("REP_ENTER:(true)#" + true + "#" + <class name> + "=" + <object ID> + "#" + "{" + <attributes> + "}" + "#" + <PC>);
%    {
%      <statements>
%    } System.err.println ("REP_END:(true)" + "#" + <class name> + "=" + <object ID> + "#" + <PC>);
% }

rule traced_true_for
  replace  [statement]
    RS [original_statement]
    
  deconstruct RS
    'for '( FI [for_init] FE [for_expression] FU [for_update] ') S [statement]
  
  deconstruct FE
  	'true ';
    
  % Current attribute list
  import attrib_list [printable_list]
  % Current ID number
  import counter [number]
  % Class name
  import NAME [id]
  % Object ID
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]
  import COLON [stringlit]
  import REP_ENTER [stringlit]
  import REP_END [stringlit]
  
  % Creates annotations
  construct EXP [stringlit]
    _ [quote FE]

  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter] 
      
  construct COMP_MSG [printable_list]
    REP_ENTER '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ 'true '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(COMP_MSG);
    
  % Updates ID information
  export counter
    counter [+ 1]
    
  construct END_MSG [printable_list]
    REP_END '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);

  % Marks statement as processed and includes annotations
  construct NB [processed_statement]
    '{
      MSG_CMD
      S
      END_MSG_CMD
    '}
  construct NF [processed_statement]
    'for '( FI FE FU ') NB

  by
    NF
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%  FOR STATEMENTS

% INPUT: 
% for ([<ini>];<cond>;[<upd>]) { <statements> }
%
% OUTPUT: 
% {
%   for ([<ini>];<cond>;[<upd>])
%      {
%        System.err.println ("REP_ENTER:(<cond>)#" + true + "#" + <class name> + "=" + <object ID> + "#" + "{" + <attributes> + "}" + "#" + <PC>);
%        {
%          <statements>
%        } System.err.println ("REP_END:(<cond>)" + "#" + <class name> + "=" + <object ID> + "#" + <PC>);
%      }
%      System.err.println ("REP_ENTER:(<cond>)#" + false + "#" + <class name> + "=" + <object ID> + "#" + "{" + <attributes> + "}" + "#" + <PC>);
%      System.err.println ("REP_END:(<cond>)" + "#" + <class name> + "=" + <object ID> + "#" + <PC>);
% }

rule traced_for
  replace  [statement]
    RS [original_statement]
    
  deconstruct RS
    'for '( FI [for_init] FE  [for_expression] FU [for_update] ') S [statement]
    
  deconstruct FE
    E [expression] ';
    
  % Current attribute list
  import attrib_list [printable_list]
  % Current ID number
  import counter [number]
  % Class name
  import NAME [id]
  % Object ID
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import LCB [stringlit]
  import RCB [stringlit]
  import LPAR [stringlit]
  import RPAR [stringlit]
  import COLON [stringlit]
  import REP_ENTER [stringlit]
  import REP_END [stringlit]
  
  % Creates annotations
  construct EXP [stringlit]
    _ [quote E]  
    
  % Includes information about values of attributes
  construct ATTR_MSG [printable_list]
    LCB '+ attrib_list '+ RCB
  % Includes ID information
  construct ID [stringlit]
    _ [quote counter]
     
  construct COMP_MSG [printable_list]
    REP_ENTER '+  COLON '+LPAR '+ EXP '+ RPAR '+ SEP '+ 'true '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(COMP_MSG);
    
  construct COMP_MSG_NOT [printable_list]
    REP_ENTER '+  COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ 'false '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ATTR_MSG '+ SEP '+ ID '+ END
  construct NOT_MSG_CMD [print_statement]
    System.err.println(COMP_MSG_NOT);
    
  % Updates ID information
  export counter
    counter [+ 1]
    
  construct END_MSG [printable_list]
    REP_END '+ COLON '+ LPAR '+ EXP '+ RPAR '+ SEP '+ NAME '+ "=" '+ OID '+ SEP '+ ID '+ END
  construct END_MSG_CMD [print_statement]
    System.err.println(END_MSG);

  % Marks statement as processed and includes annotations
  construct NB [processed_statement]
    '{
      MSG_CMD
      S
      END_MSG_CMD
    '}
  construct NF [processed_statement]
    'for '( FI FE FU ') NB

  construct NS [processed_statement]
    '{
      NF
      NOT_MSG_CMD
      END_MSG_CMD
    '}

  by
    NS
end rule