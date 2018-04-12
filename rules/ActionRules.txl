% Author: Lucio Mauro Duarte

% Traces user-defined events

% INPUT: 
% #action:"<action_name>";
% OUTPUT: 
% System.err.println("ACTION:" + "<action_name>" + "#" + NAME + '.  + "=" + OID);
rule traced_user_action
  replace [statement]
    RS [original_statement]

  deconstruct RS
   '# 'action ': A [printable_list] ';
  
  import NAME [id]
  import OID [printable_list]
  import ACTION [stringlit]
  import COLON [stringlit]
  import SEP [stringlit]
  import END [stringlit]
    
  % Creates annotation
  construct MSG_ACT [printable_list]
    ACTION '+ COLON '+ A '+ SEP '+ NAME '+ "=" '+ OID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(MSG_ACT);
  
  % Marks statement as processed and includes annotation
  construct NS [processed_statement]
  	MSG_CMD
  by
    NS
end rule

% Traces user-defined input values

% INPUT: 
% #input:<input_action>;
% OUTPUT: 
% System.err.println("ACTION:in.<input_action>" + "#" + NAME + "=" + OID);

rule traced_user_input
  replace [statement]
    RS [original_statement]

  deconstruct RS
   '# 'input ': I [printable_list] ';
  
  import NAME [id]
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import COLON [stringlit]
  import IN_ACTION [printable_list]
    
  % Creates annotation
  construct MSG_ACT [printable_list]
    IN_ACTION '+ I '+ SEP '+ NAME '+ "=" '+ OID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(MSG_ACT);
  
  % Marks statement as processed and includes annotation
  construct NS [processed_statement]
  	MSG_CMD
  by
    NS
end rule

% Traces user-defined output values

% INPUT: 
% #output:"<output_action>";
% OUTPUT: 
% System.err.println("ACTION:out." + "<output_value>" + "#" + NAME + "=" + OID);

rule traced_user_output
  replace [statement]
    RS [original_statement]

  deconstruct RS
   '# 'output ': O [printable_list] ';
  
  import NAME [id]
  import OID [printable_list]
  import SEP [stringlit]
  import END [stringlit]
  import COLON [stringlit]
  import OUT_ACTION [printable_list]
    
  % Creates annotation
  construct MSG_ACT [printable_list]
    OUT_ACTION '+ O '+ SEP '+ NAME '+ "=" '+ OID '+ END
  construct MSG_CMD [print_statement]
    System.err.println(MSG_ACT);
  
  % Marks statement as processed and includes annotation
  construct NS [processed_statement]
  	MSG_CMD
  by
    NS
end rule

