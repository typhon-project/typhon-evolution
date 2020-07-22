module lang::typhonevo::EvoCompiler

import IO;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::handlers::EntityEvolution;
import lang::typhonevo::handlers::AttributeEvolution;
import lang::typhonevo::handlers::RelationEvolution;
import lang::typhonml::XMIReader;
import lang::typhonml::Util;
import lang::typhonml::TyphonML;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonevo::handlers::MergeEntity;

EvoSyntax evolve(EvoSyntax x, loc location){

	str xmi = readFile(location + "<extract_path(x)>");
	Model m = xmiString2Model(xmi);
	Schema s = model2schema(m);
	
	// Init all queries to unchanged
	x = visit(x){
		case EvoQuery q => setStatusUnchanged(q)
	};

	for ( ChangeOp op <- s.changeOperators){
		x = visit(x){
			case EvoQuery q => transform(q, op, s)
		};
	};
	
	return x;
}

EvoQuery transform(q:(EvoQuery)`BROKEN  <QlQuery _>`, _, _) = q;
EvoQuery transform(q:(EvoQuery)`BROKEN <Annotation+ _>  <QlQuery _>`, _, _) = q;

// Listing all the change operators

// ENTITIES (/!\ manque split)
EvoQuery transform(EvoQuery q, <"addEntity", _>, Schema s) = q;
EvoQuery transform(EvoQuery q, <"renameEntity", [old_name, new_name]>, Schema s) = entity_rename(q, old_name, new_name);
EvoQuery transform(EvoQuery q, <"removeEntity", [name]>, Schema s) = entity_remove(q, name);
EvoQuery transform(EvoQuery q, <"mergeEntity", [e1, e2, relation]>, Schema s) = entity_merge(q, relation, e1, e2, s);
EvoQuery transform(EvoQuery q, <"migrateEntity", [entity, db]>, Schema s) = entity_migration(q, entity);

// ATTRIBUTES
EvoQuery transform(EvoQuery q, <"renameAttribute", [entity, old_name, new_name]>, Schema s) = attribute_rename(q, entity, old_name, new_name, s);
EvoQuery transform(EvoQuery q, <"removeAttribute", [entity, attr]>, Schema s) = attribute_remove(q, entity, attr, s);
EvoQuery transform(EvoQuery q, <"changeAttributeType", [entity, attr, t]>, Schema s) = attribute_type_change(q, entity, attr, t, s);
EvoQuery transform(EvoQuery q, <"addAttribute", [entity, attr, t]>, Schema s) = attribute_add(q, attr, entity);

// RELATIONS
EvoQuery transform(EvoQuery q, <"renameRelation", [entity, old_name, new_name]>, Schema s) = rename_relation(q, entity, old_name, new_name);
EvoQuery transform(EvoQuery q, <"removeRelation", [entity, name]>, Schema s) = remove_relation(q, entity, name);
EvoQuery transform(EvoQuery q, <"changeRelationContainement", [name, containment]>, Schema s) = change_containment(q, name, containment);
EvoQuery transform(EvoQuery q, <"changeRelationCardinality", [name, cardinality]>, Schema s) = change_cardinality(q, name, cardinality);



list[EvoChangeOp] extract_op(EvoSyntax x) = [ c | /EvoChangeOp c := x];
list[EvoQuery] extract_queries(EvoSyntax x) = [ c | /EvoQuery c := x];

Path extract_path(EvoSyntax x) = [c | /Path c := x][0];


