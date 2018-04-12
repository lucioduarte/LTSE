% Author: Lucio Mauro Duarte
% Version: 23/07/2013


% Defines a format for printing values of attributes
define local_attribute
	  'this '.  [id]
	| [id] '. [id]
end define

% Defines printable tokens
define printable
    [stringlit]
  | [charlit]
  | [number]
  | [expression]
  | [attribute]
  | [id]
  | [local_attribute]
end define

% Defines a list of printable tokens
define printable_list
    [printable]
  | [printable_list] '+ [printable_list]
  | [empty]
end define

% Define a print statement, used to annotate control flow statements
define print_statement
  'System '. 'err '. 'println '( [printable_list] ') '; [NL]
end define

% Create a user-defined comment to identify events
define user_statement
  '# 'action ': [printable_list] ';
end define

% Define an input statement, used to capture input commands
define user_input_statement
  '# 'input ': [printable_list] '; [NL]
end define

% Define an output statement, used to capture outputs
define user_output_statement
  '# 'output ': [printable_list] '; [NL]
end define

% Defines statements as original (used to recognise statements not
% yet parsed)
define original_statement
    [label_statement]
  | [empty_statement]
  | [expression_statement]
  | [if_statement]
  | [switch_statement]
  | [while_statement]
  | [do_statement]
  | [for_statement]
  | [break_statement]
  | [continue_statement]
  | [return_statement]
  | [throw_statement]
  | [synchronized_statement]
  | [try_statement]
  | [block] [NL] [NL]
  | [comment_NL]
  | [user_statement]
  | [user_input_statement]
  | [user_output_statement]
end define

% Defines statements as processed
define processed_statement
    [original_statement]
  | [print_statement]
  | [empty]
end define

% Redefines statements as original, processed or empty
redefine statement
    [original_statement]
  | [processed_statement]
end redefine

% Defines method declarations as original (used to recognise method
% declarations not yet parsed)
define original_method_declaration
  [NL] [repeat modifier] [type_specifier] [method_declarator] [opt
  throws] [method_body]
end define

% Defines method declarations as processed (i.e., already parsed)
define processed_method_declaration
    [original_method_declaration]
  | [empty]
end define

% Redefines method declarations as original, processed or empty
redefine method_declaration
    [original_method_declaration]
  | [processed_method_declaration]
end redefine

% Defines method bodies as original (used to recognise method
% bodies not yet parsed)
define original_method_body
    [block] [NL][NL]
  | ';      [NL][NL]
end define

% Defines method bodies as processed (i.e., already parsed)
define processed_method_body
    [original_method_body]
  | [empty]
end define

% Redefines method bodies as original, processed or empty
redefine method_body
    [original_method_body]
  | [processed_method_body]
end redefine

% Defines declarations as original (used to recognise declarations
% not yet parsed)
define original_declaration
    [local_variable_declaration]
  | [class_declaration]
end define

% Defines declarations as processed (i.e., already parsed)
define processed_declaration
    [original_declaration]
  | [print_statement]
  | [empty]
end define

% Redefines declarations
redefine declaration_or_statement
    [original_declaration]
  | [processed_declaration]
  | [statement]
end redefine

% Attributes
define attribute
  [id]
end define

% Defines user-defined attributes
define user_attribute
  '# 'attribute ': [printable_list] '= [expression] '; [NL]
end define

% Defines attributes as original (used to recognise attributes
% not yet parsed)
define original_attribute
    [repeat modifier] [type_specifier] [variable_declarators] '; [NL]
  | [user_attribute]
end define

% Defines declarations as processed (i.e., already parsed)
define processed_attribute
    [original_attribute]
  | [empty]
end define

% Redefines a variable declaration so that an attribute can be
% either original or processed
redefine variable_declaration
    [original_attribute]
  | [processed_attribute]
end redefine

% Defines an original class header
define original_class_header
	[repeat modifier] 'class [class_name] [opt extends_clause] [opt implements_clause]
end define
	
% Defines a processed class _header
define processed_class_header
	[original_class_header]
  | [empty]
end define
  
% Redefines class header
redefine class_header
  	[original_class_header]
  |	[processed_class_header]
end redefine

% Defines return statement as original
define original_return
  'return [opt expression] ';      [NL]
end define

% Defines processed return statements
define processed_return
    [original_return]
  | [block] %'{ [NL] [print_statement] [print_statement] [processed_return] '}
  | [empty]
end define

% Redefines return statement
redefine return_statement
    [original_return]
  | [processed_return]
end redefine

define original_class_or_interface_body
	'{                                    [NL][IN]
     [repeat class_body_declaration]    [EX]
  '} [opt ';]                           [NL][NL]
end define

define processed_class_or_interface_body
		[original_class_or_interface_body]
end define

redefine class_or_interface_body
		[original_class_or_interface_body]
	|	[processed_class_or_interface_body]
	| [empty]
end redefine
