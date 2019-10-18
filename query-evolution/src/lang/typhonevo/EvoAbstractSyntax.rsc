module lang::typhonevo::EvoAbstractSyntax

extend lang::typhonql::Query;

lexical EvoID = [a-z][a-z0-9]* !>> [a-z0-9];

start syntax EvoSyntax 
	= evosyntax: {ChangeOperator ","}+ operators {Query ","}* queries;
	

syntax ChangeOperator
	= changeoperator: Object obj Operation op;
	
syntax Object
	= entity: "Entity";
	

syntax Operation
	= add: "Add" EvoID name
	| rename: "Rename" EvoID oldName "to" EvoID newName
	;
