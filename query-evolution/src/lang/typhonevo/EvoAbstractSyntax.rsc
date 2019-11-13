module lang::typhonevo::EvoAbstractSyntax

extend lang::typhonql::Query;
extend lang::std::Id;
extend lang::std::ASCII;

// ML syntax : https://github.com/typhon-project/typhonml/blob/master/it.univaq.disim.typhonml.parent/bundles/it.univaq.disim.typhonml.xtext/src/it/univaq/disim/typhon/TyphonML.xtext

lexical Annotation = "#@" (![@] | "@" !>> "#")* "@#";

start syntax EvoSyntax 
	= evosyntax: "changeOperators" "[" ChangeOperator* "]"operators {EvoQuery ","}* queries;
	

syntax EvoQuery
	= annotatedQuery: Annotation an Query q
	| query : Query
	;

syntax ChangeOperator
	= entityOperator: EntityOperation op
	| attributeOperator : AttributesOperations op
	| relationOperator: RelationOperations op
	| databaseOperator: DatabaseOperations op
	;
	

syntax EntityOperation
	= add: NameSpace? "add" "entity" EId name
	| remove: "remove" "entity" EId name
	| rename: "rename" "entity" EId oldName "as" EId newName
	| splitEntity: "split" "entity" EId name "{" "left" EId entity1 "right" EId entity2 "}"
 	| merge: "merge" "entities" EId entity1 EId entity2 "as" EId new_name
 	| migrate: "migrate" "entity" EId entity "to" EId db
	;
	
syntax AttributesOperations
	= add: NameSpace? "add" "attribute" Id name ":" EId type "to" EId entity
	| rename: "rename" "attribute" Id name "as" Id new_name 
	| remove: "remove" "attribute" Id attribute
	| changeType: 'change' 'attribute' Id attribute 'type' EId type
	| addToIndex: 'extends' 'tableindex' EId entity '{' {Id ","}+ '}'
	| removeFromIndex: 'reduce' 'tableindex' EId entity '{' {Id ","}+  '}';
	
syntax RelationOperations
	= add: NameSpace? 'add' 'relation' Id relation 'to' EId entity ':'? '-\>' EId "."? Id? '[' Cardinality ']'
	| rename: "rename" "relation" Id old_name "as" EId new_name
	| remove: "remove" "relation" Id to_remove
	| changeContainement: 'change' 'containment' Id relation 'as' Bool
	| changeCardinality: 'change' 'cardinality' Id relation 'as' Cardinality
	;
	
syntax DatabaseOperations
	= drop: 'drop' 'tableindex' Id
	| rename: 'rename' 'table' Id table_name 'as' Id new_name
	| addToIndex: 'extends' 'tableindex' Id table '{' {Id ","}* '}'
	| renamCollection : 'rename' 'collection' Id collection_name 'as' Id new_name
	;

syntax NameSpace 
	= "importedNamespace" Id;

syntax Cardinality
	= zero_one: '0..1'
	| card_one: "1"
	| zero_many: '0..*' 
	| one_many: '*';

	

 

