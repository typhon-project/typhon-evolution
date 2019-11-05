module lang::typhonevo::EvoAbstractSyntax

extend lang::typhonql::Query;
extend lang::std::Id;
extend lang::std::ASCII;

lexical Annotation = "#@" (![@] | "@" !>> "#")* "@#";

start syntax EvoSyntax 
	= evosyntax: "changeOperators" "[" ChangeOperator* "]"operators {EvoQuery ","}* queries;
	

syntax EvoQuery
	= annotatedQuery: Annotation an Query q
	| query : Query
	;

syntax ChangeOperator
	= entityOperator: EntityOperation op
	;
	

syntax EntityOperation
	= add: "add" "entity" EId name
	| remove: "remove" "entity" EId name
	| rename: "rename" "entity" EId oldName "as" EId newName
	| splitEntity: "split" "entity" EId name "{" "left" EId entity1 "right" EId entity2 "}"
 	| merge: "merge" "entities" EId entity1 EId entity2 "as" EId new_name
 	| migrate: "migrate" "entity" EId entity "to" EId db
	;


