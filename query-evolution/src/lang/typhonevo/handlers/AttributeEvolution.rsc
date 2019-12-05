module lang::typhonevo::handlers::AttributeEvolution

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonevo::utils::SchemaUtils;
import lang::typhonml::Util;


// DISPATCHERS

EvoQuery evolve_attribute(EvoQuery q, (AttributesOperations) `rename attribute  <Id old_id> as <Id new_id>`, Schema s)
	= attribute_rename(q, old_id, new_id);
	
EvoQuery evolve_attribute(EvoQuery q, (AttributesOperations) `remove attribute <Id attribute>`, Schema s)
	= attribute_remove(q, attribute, s);

default EvoQuery evolve_attribute(EvoQuery q, _, _) = q;


// HANDLERS 

EvoQuery attribute_rename(EvoQuery q, Id old_name, Id new_name){
	// TODO verify the entity owning this attribute
	
	req = visit(q){
		case old_name => new_name
	};
	
	return req;
}

EvoQuery attribute_remove(EvoQuery q, Id name, Schema s){
	//TODO check if the attribute is called explicitly. 
	
	Query query;
	matched = false;
	
	visit(q){
		case Query qu:{
			query = qu;
		}
		case name :{
			matched = true;
		}
	}
	
	if(matched){
		return parse(#EvoQuery, "#@ Attribute <name> is removed. That query is broken @# <query>");
	}
	return q;
}



