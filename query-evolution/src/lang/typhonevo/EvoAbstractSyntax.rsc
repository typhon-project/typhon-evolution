module lang::typhonevo::EvoAbstractSyntax

extend lang::typhonql::Query;
extend lang::typhonql::DML;
extend lang::std::Id;

// ML syntax : https://github.com/typhon-project/typhonml/blob/master/it.univaq.disim.typhonml.parent/bundles/it.univaq.disim.typhonml.xtext/src/it/univaq/disim/typhon/TyphonML.xtext

lexical Annotation = "#@" (![@] | "@" !>> "#")* "@#";
lexical Path = (![\t \n \a0B \a0C \r \ ;])*;

start syntax EvoSyntax 
	= evosyntax: Import import "changeOperators" "[" EvoChangeOp* "]"operators {EvoQuery ","}* queries;
	

syntax EvoQuery
	= annotatedQuery: Status s QlQuery q
	| flagged: Status s  Annotation+ annots  QlQuery q
	| query : QlQuery q
	;

syntax Import = "import" Path path ";";

syntax Status
	= "MODIFIED"
	| "WARNING"
	| "BROKEN"
	| "UNCHANGED"
	;


syntax QlQuery
	= Query query
	| Statement query;

syntax EvoChangeOp
	= entityOperator: EntityOperation op
	| attributeOperator : AttributesOperations op
	| relationOperator: RelationOperations op
	| databaseOperator: DatabaseOperations op
	;
/*	
syntax EntityOperation = Kind "entity" EId currentName Operation;

lexical Kind
	= "add"
	| "remove"
	;
	
syntax Operation
  = "{" "}"
  | "as" EId newName
  ;
 */

syntax EntityOperation
	= add: NameSpace? "add" "entity" EId name "{"  "}"// Nothing
	| remove: "remove" "entity" EId name // Done
	| rename: "rename" "entity" EId oldName "as" EId newName // Done
	| splitVertical: "split" "entity" "vertical" EId entity1 "to" EId entity2 "attributes" ":" "[" {Expr ","}+ "]"
	| splitEntity: "split" "entity" EId name "{" "left" EId entity1 "right" EId entity2 "}" //TODO (waiting for new syntax)
 	| merge: "merge" "entities" EId entity1 EId entity2 "as" EId new_name 
 	| migrate: "migrate" "entity" EId entity "to" EId db // Nothing
	;
	
syntax AttributesOperations
	= add: NameSpace? "add" "attribute" Id name ":" EId type "to" EId entity //Nothing
	| rename: "rename" "attribute" Id name "from" EId entity "as" Id new_name //TODO Missing entity information
	| remove: "remove" "attribute" Id attribute//TODO Missing entity information
	| changeType: 'change' 'attribute' Id attribute 'type' EId type//NOTHING
	| addToIndex: 'extends' 'tableindex' EId entity '{' {Id ","}+ '}' //NOTHING
	| removeFromIndex: 'reduce' 'tableindex' EId entity '{' {Id ","}+  '}'; //NOTHING
	
syntax RelationOperations
	= add: NameSpace? 'add' 'relation' Id relation 'to' EId entity ':'? '-\>' EId "."? Id? '[' Cardinality ']' //NOTHING
	| rename: "rename" "relation" Id old_name "as" Id new_name //NOTHING
	| remove: "remove" "relation" Id to_remove //TODO Missing information or other
	| changeContainement: 'change' 'containment' Id relation 'as' Bool //NOTHING
	| changeCardinality: 'change' 'cardinality' Id relation 'as' Cardinality //NOTHING
	;
	
syntax DatabaseOperations
	= drop: 'drop' 'tableindex' Id //NOTHING
	| rename: 'rename' 'table' Id table_name 'as' Id new_name //NOTHING
	| addToIndex: 'extends' 'tableindex' Id table '{' {Id ","}* '}' //NOTHING
	| renamCollection : 'rename' 'collection' Id collection_name 'as' Id new_name //NOTHING
	;

syntax NameSpace 
	= "importedNamespace" Id;

syntax Cardinality
	= zero_one: '0..1'
	| card_one: "1"
	| zero_many: '0..*' 
	| one_many: '*';

	

 

