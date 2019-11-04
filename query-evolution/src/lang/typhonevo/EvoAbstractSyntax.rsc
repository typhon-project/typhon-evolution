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
	= entityOperator: EntityOperation op
	;
	

syntax EntityOperation
	= add: "Entity" "Add" EId name
	| rename: "Entity" "Rename" EId oldName "to" EId newName
	| remove: "Entity" "Remove" EId name
	| splitVertical: "Entity" "Split Vertical" EId name "to" EId entity1 "," EId entity2
	| splitHorizontal: "Entity" "Split horizontal" EId name
 	| merge: "Entity" "Merge" EId entity1 "and" EId entity2 "to" EId new_name
 	| migrate: "Entity" "Migrate" EId
	;


