module lang::typhonevo::EvoAbstractSyntax

extend lang::typhonql::Query;
extend lang::std::Id;

//layout Layout = WhitespaceAndComment* !>> [\ \t\n\r%];

start syntax EvoSyntax 
	= evosyntax: {ChangeOperator ","}+ operators {Query ","}* queries;
	

syntax ChangeOperator
	= changeoperator: Object obj Operation op;
	
syntax Object
	= entity: "Entity"
	| relation: "Relation"
	;
	

syntax Operation
	= add: "Add" EId name
	| rename: "Rename" EId oldName "to" EId newName
	;
