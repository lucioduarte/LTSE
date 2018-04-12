% Author: Lucio Mauro Duarte


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Ignores final static attributes (1)

% INPUT: 
% final static <type> <id> [<dimension>] [= <exp>];
% OUTPUT: 
% INPUT marked as processed
rule ignore_final_static_attribute
  replace [field_declaration]
    RS [original_attribute]

  deconstruct RS
    'final 'static T [type_specifier] N [id] D [repeat
    dimension] E [opt equals_variable_initializer] ';

  construct NS [processed_attribute]
    'final 'static T N D E ';
  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Ignores static final attributes (2)

% INPUT: 
% static final <type> <id> [<dimension>] [= <exp>];
% OUTPUT: 
% INPUT marked as processed
rule ignore_static_final_attribute
  replace [field_declaration]
    RS [original_attribute]

  deconstruct RS
    'static 'final T [type_specifier] N [id] D [repeat
    dimension] E [opt equals_variable_initializer] ';

  construct NS [processed_attribute]
    'static 'final T N D E ';
  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Ignores final attributes (3)

% INPUT: 
% final <type> <id> [<dimension>] [= <exp>];
% OUTPUT: 
% INPUT marked as processed
rule ignore_final_attribute
  replace [field_declaration]
    RS [original_attribute]

  deconstruct RS
    'final T [type_specifier] N [id] D [repeat 
    dimension] E [opt equals_variable_initializer] ';
    
  construct NS [processed_attribute]
    'final T N D E ';
  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Collects information about static attributes (4)

% INPUT: 
% static <modifier> <type> <id> [<dimension>] [= <exp>];
% OUTPUT: 
% INPUT marked as processed and [id] added to 
% list of attributes

rule obtain_static_modifier_attribute
  replace [field_declaration]
    RS [original_attribute]

  deconstruct RS
    'static M [modifier] T [type_specifier] N [id] D [repeat
    dimension] E [opt equals_variable_initializer] ';
    
  % Recovers current list of attributes
  import attrib_list [printable_list]
  import ATTR [stringlit]
  import CLASS_NAME [id]
  
  construct ATTR_VAL [local_attribute]
  	CLASS_NAME '. N
    
  construct ATTR_MSG [stringlit]
    _ [quote N]
  construct NewAttribute [printable_list]
    ATTR_MSG '+ "=" '+ ATTR_VAL '+ ATTR

  % Returns list of attributes appended with new attribute
  export attrib_list
    attrib_list '+ NewAttribute

  construct NS [processed_attribute]
    'static M T N D E ';

  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Collects information about static attributes with modifier (5)
% 

% INPUT: 
% <modifier> static <type> <id> [<dimension>] = <exp>;
% OUTPUT: 
% INPUT marked as processed and [id] added to 
% list of attributes

rule obtain_modifier_static_attribute
  replace [field_declaration]
    RS [original_attribute]

  deconstruct RS
    M [modifier] 'static T [type_specifier] N [id] D [repeat
    dimension] E [opt equals_variable_initializer] ';
    
  % Recovers current list of attributes
  import attrib_list [printable_list]
  import ATTR [stringlit]
  import CLASS_NAME [id]
  
  construct ATTR_VAL [local_attribute]
  	CLASS_NAME '. N
    
  construct ATTR_MSG [stringlit]
    _ [quote N]
  construct NewAttribute [printable_list]
    ATTR_MSG '+ "=" '+ ATTR_VAL '+ ATTR

  % Returns list of attributes appended with new attribute
  export attrib_list
    attrib_list '+ NewAttribute

  construct NS [processed_attribute]
    M 'static T N D E ';

  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Collects information about attributes with modifiers (6)

% INPUT: 
% <modifier> <type> <id> [<dimension>] [= <exp>];
% OUTPUT: 
% INPUT marked as processed and [id] added to 
% list of attributes

rule obtain_modifier_attribute
  replace [field_declaration]
    RS [original_attribute]

  deconstruct RS
    M [modifier] T [type_specifier] N [id] D [repeat
    dimension] E [opt equals_variable_initializer]';
    
  % Recovers current list of attributes
  import attrib_list [printable_list]  
  import ATTR [stringlit]
  
  construct ATTR_VAL [local_attribute]
  	'this '. N

  construct ATTR_MSG [stringlit]
    _ [quote N]
  construct NewAttribute [printable_list]
    ATTR_MSG '+ "=" '+ ATTR_VAL '+ ATTR

  % Returns list of attributes appended with new attribute
  export attrib_list
    attrib_list '+ NewAttribute

  construct NS [processed_attribute]
    M T N D E ';

  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Collects information about attributes without modifiers (7)

% INPUT: 
% <type> <id> [<dimension>] [= <exp>];
% OUTPUT: 
% INPUT marked as processed and [id] added to 
% list of attributes

rule obtain_attribute
  replace [field_declaration]
    RS [original_attribute]

  deconstruct RS
    T [type_specifier] N [id] D [repeat dimension] E [opt equals_variable_initializer]';
    
  % Recovers current list of attributes
  import attrib_list [printable_list]  
  import ATTR [stringlit]
  
  construct ATTR_VAL [local_attribute]
  	'this '. N

  construct ATTR_MSG [stringlit]
    _ [quote N]
  construct NewAttribute [printable_list]
    ATTR_MSG '+ "=" '+ ATTR_VAL '+ ATTR

  % Returns list of attributes appended with new attribute
  export attrib_list
    attrib_list '+ NewAttribute

  construct NS [processed_attribute]
    T N D E ';

  by
    NS
end rule

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Obtains user-defined attributes (8)

% INPUT: 
% #attribute:<attr_name> = <exp>;
% OUTPUT: 
% Removes INPUT and adds <attr_name> to list of
% attributes

rule obtain_user_attribute
  replace [field_declaration]
    RS [user_attribute]
    
  deconstruct RS
   '# 'attribute ': N [printable_list] '= E [expression] ';
    
  % Recovers current list of attributes
  import attrib_list [printable_list]
  import ATTR [stringlit]

  construct NewAttribute [printable_list]
    N '+ "=" '+ '( E ') '+ ATTR

  % Returns list of attributes appended with new attribute
  export attrib_list
    attrib_list '+ NewAttribute  
      
  by
    _
end rule
