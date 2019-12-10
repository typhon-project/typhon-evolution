module lang::typhonevo::handlers::AttributeEvolution

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonevo::utils::SchemaUtils;
import lang::typhonml::Util;
import lang::typhonevo::utils::EvolveStatus;

// DISPATCHERS

EvoQuery evolve_attribute(EvoQuery q, (AttributesOperations) `rename attribute  <Id old_id> from <EId entity> as <Id new_id>`, Schema s)
	= attribute_rename(q, old_id, new_id, entity);
	
EvoQuery evolve_attribute(EvoQuery q, (AttributesOperations) `remove attribute <Id attribute> from <EId entity>`, Schema s)
	= attribute_remove(q, attribute, entity, s);

default EvoQuery evolve_attribute(EvoQuery q, _, _) = q;


// HANDLERS 

EvoQuery attribute_rename(EvoQuery q, Id old_name, Id new_name, EId entity){
	
	// Get mapping
	
	// Construct expr
	
	// check if expr in query
	req = visit(q){
		case old_name => new_name
	};
	
	if(req := q)
		return q;
	
	return setStatusChanged(req);
}

EvoQuery attribute_remove(EvoQuery q, Id name, EId entity, Schema s){
	//TODO check if the attribute is called explicitly. 
	
	Query query;
	matched = false;
	
	visit(q){
		case name :{
			matched = true;
		}
	}
	
	// Err if use in where clause. Warning otherwise
	if(matched){
		setStatusWarn(q, "Query might change, Attribut <name> was removed");
	}
	return q;
}



