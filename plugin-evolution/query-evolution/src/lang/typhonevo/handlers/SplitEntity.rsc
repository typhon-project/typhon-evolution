module lang::typhonevo::handlers::SplitEntity

import IO;
import List;
import String;
import Set;
import ParseTree;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonql::Expr;
import lang::typhonevo::utils::QueryManipulation;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonevo::utils::SchemaUtils;
import lang::typhonevo::utils::Util;
import lang::typhonml::TyphonML;

import lang::typhonml::Util;

EvoQuery split_vertical(EvoQuery q,  str entity, str new_entity, list[str] attributes, Schema s){
	
	e1 = parse(#EId, entity);
	e2 = parse(#EId, new_entity);
	
	// Filter the query using the impacted entity
	relevant = false;
	
	for(/(EId) `<EId id>` := q){
		if(id == e1)
			relevant = true;
	}
	
	if(!relevant)
		return q;

	switch(q.q.query){
		case (Statement) `insert <{Obj ","}* obj>` : 
			return setStatusBroken(q, "Entity <e1> split into <e1>, <e2>. Two insert queries are probably needed.");
		case (Statement) `delete <Binding binding> <Where? where>` : 
			return setStatusBroken(q,"Entity <e1> split into <e1>, <e2>. Two delete queries are probably needed.");
		case (Statement) `update <Binding binding> <Where? where> set  { <{KeyVal ","}* keyVals> }` : 
			return setStatusBroken(q, "Entity <e1> split into <e1>, <e2>. The update cannot be automatically adapted.");
		case Query query : 
			return handle_split_query(q, e1, e2, attributes, s);
	};
}

EvoQuery handle_split_query(EvoQuery q, EId e1, EId e2, list[str] extracted_attributes, Schema s){

	// Retrieving useful info from the query 
	
	use_both_entity_attribute = contains_select_all(e1, q);
	attributes = get_attrs_used(e1, q);
	
	use_new_entity_attrs = false;
	use_other_attrs = false;
	
	for(Id i <- attributes){
		tmp = false;
		
		for(str attribute <-extracted_attributes){
			attr = parse(#Id, attribute);
			
			if(i == attr)
				tmp = true;
		}
		
		if(tmp)
			use_new_entity_attrs= true;
		else
			use_other_attrs = true;
	}
	
	if(use_new_entity_attrs && use_other_attrs){
		use_both_entity_attribute = true;
	}
	
	// use attributes of both entity
	if(use_both_entity_attribute){
	
		// Add binding for the new entity 
		bind = toLowerCase("<e2>");
		binding = parse(#Binding, "<e2> <bind>");
		e = addBinding(q, binding);
		
		// Add join
		b = get_bindings(q)[e1];
		expr = parse(#Expr, "<b>.to_<e2> == <bind>");
		e = addWhereCondition(e, expr);
		
		// Call new entity attributes with correct alias
		
		for(str attribute <-extracted_attributes){
			attr = parse(#Id, attribute);
			
			old_reference = parse(#Expr, "<b>.<attr>");
			new_reference = parse(#Expr, "<bind>.<attr>");
			
			e = visit(e){
				case old_reference => new_reference
			}
		
		}
		
		return setStatusWarn(e, "Entity <e1> split into <e1> <e2>");
	}
		
	
	// Only attributes of old entity used
	if(use_other_attrs)
		return setStatusWarn(q, "Entity <e1> split into <e1> <e2>");
		
	// Only new entity used
	
	EvoQuery e = visit(q){
		case e1 => e2
	};
	
	return setStatusWarn(e, "Entity <e1> split into <e1> <e2>");
}
