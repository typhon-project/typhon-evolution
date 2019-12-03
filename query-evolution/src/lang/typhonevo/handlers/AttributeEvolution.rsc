module lang::typhonevo::handlers::AttributeEvolution

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;


EvoQuery evolve_attribute(EvoQuery q, EntityOperation op){
	switch(op){
		case (AttributesOperations) `rename attribute  <Id old_id> as <Id new_id>`:{
		 	return attribute_rename(q, old_id, new_id);
		}
		case (AttributesOperations) `remove attribute <Id entity>`: {
			return attribute_remove(q, entity);
		}
	};
	
	return q;
}


EvoQuery attribute_rename(EvoQuery q, Id old_name, Id new_name){
	// TODO verify the entity owning this attribute
	
	req = visit(q){
		case old_name => new_name
	};
	
	return req;
}

EvoQuery attribute_remove(EvoQuery q, Id name){
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



