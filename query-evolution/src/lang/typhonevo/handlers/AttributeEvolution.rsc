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
	= attribute_rename(q, old_id, new_id, s);
	
EvoQuery evolve_attribute(EvoQuery q, (AttributesOperations) `remove attribute <Id attribute> from <EId entity>`, Schema s)
	= attribute_remove(q, attribute, entity, s);

default EvoQuery evolve_attribute(EvoQuery q, _, _) = q;


// HANDLERS 

EvoQuery attribute_rename(EvoQuery q, Id old_name, Id new_name, Schema s){
	
	// Select the first entity containing the attributes. will be updated when the parsing 
	// of the change operators in the xmi will be completed
	
	entity = {from | <from, "<old_name>", _>  <- s.attrs}[0];
	
	req = visit(q){
		case old_name => new_name
	};
	
	if(req := q)
		return q;
	
	return setStatusChanged(req);
}

EvoQuery attribute_remove(EvoQuery q, Id name, Schema s){
	//TODO check if the attribute is called explicitly. 

	matched = false;
	
	visit(q){
		case name :{
			matched = true;
		}
	}
	
	// Err if use in where clause. Warning otherwise
	if(matched){
		setStatusWarn(q, "Query set might change, Attribut <name> was removed");
	}
	return q;
}

EvoQuery attribute_type_change(EvoQuery q, Id name, Schema s){
	matched = false;
	
	visit(q){
		case name :{
			matched = true;
		}
	}
	
	if(matched){
		setStatusWarn(q, "The type of the attribute <name> changed");
	}
	return q;
}



