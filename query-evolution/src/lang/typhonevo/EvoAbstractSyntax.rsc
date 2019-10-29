module lang::typhonevo::EvoAbstractSyntax

extend lang::typhonql::Query;
extend lang::std::Id;
extend lang::std::ASCII;

lexical Annotation = "#@" (![@] | "@" !>> "#")* "@#";

start syntax EvoSyntax 
	= evosyntax: {ChangeOperator ","}* operators {EvoQuery ","}* queries;
	

syntax EvoQuery
	= annotatedQuery: Annotation an Query q
	| query : Query
	;

syntax ChangeOperator
	= changeoperator: Object obj Operation op;
	
syntax Object
	= entity: "Entity"
	| relation: "Relation"
	;
	

syntax Operation
	= add: "Add" EId name
	| rename: "Rename" EId oldName "to" EId newName
	| remove: "Remove" EId name
	| splitVertical: "Split vertical" EId name "to" EId entity1 "," EId entity2
	| splitHorizontal: "Split horizontal" EId name
 	| merge: "Merge" EId entity1 "and" EId entity2
 	| migrate: "Migrate" EId
	;
	

