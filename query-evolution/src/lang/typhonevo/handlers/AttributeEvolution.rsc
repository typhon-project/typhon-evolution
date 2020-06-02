module lang::typhonevo::handlers::AttributeEvolution

import IO;
import ParseTree;
import List;
import Set;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonevo::utils::SchemaUtils;
import lang::typhonml::Util;
import lang::typhonevo::utils::QueryManipulation;


// HANDLERS 

EvoQuery attribute_rename(EvoQuery q, str entity, Id old_name, Id new_name, Schema s){

	eid = parse(#EId, entity);
	
	if(use_entity(q, eid)){
		req = visit(q){
			case old_name => new_name
		};
		
		if(req := q)
			return q;
		
		return setStatusChanged(req);
	}
	
	return q;
}

EvoQuery attribute_remove(EvoQuery q, str entity, Id name, Schema s){
	//TODO check if the attribute is called explicitly. 
	
	eid = parse(#EId, entity);
	
	if(use_entity(q, eid)){
		
		// Check if the attribute is called directly
		if(explicit_use(q.q.query, eid, name)){
			return setStatusBroken(q, "Attribute <eid>.<name> removed");
		}
		
		return setStatusWarn(q, "Query result might differ : Attribute <eid>.<name> removed");
	}

	return q;
}

EvoQuery attribute_type_change(EvoQuery q, str entity, Id name, EId t, Schema s){

	// Select the first entity containing the attributes. will be updated when the parsing 
	// of the change operators in the xmi will be completed
	eid = parse(#EId, entity);
	
	
	if(use_entity(q, eid)){
		return setStatusWarn(q, "The type of the attribute <name> from <eid> changed");
	}
	
	return q;
}


EvoQuery attribute_add(EvoQuery q, Id attr, EId entity){

	switch(q.q.query){
		case s:(Statement) `insert <{Obj ","}* obj>`:{
			for(/(EId) e := s){
				if(e := entity)
					return setStatusBroken(q, "Attribute <attr> added to <entity>. Insert is broken");
			}
		}
		case s:(Statement) `delete <EId ent> <VId var> <Where? where>`:{
			if(ent := entity)
				return setStatusWarn(q, "Attribute <attr> added to <entity>. You may delete more information than expected");
		}
		case Query s:{
			for(/(EId) e := s){
				if(e := entity)
					return setStatusWarn(q, "Attribute <attr> added to <entity>. Result of the query may have changed");
			}
		}
	}
	
	return q;
}


bool explicit_use(Query q, EId ent, Id attr) {
	println(q);

	map[EId, VId] binding = ();

	for(/(Binding) `<EId entity> <VId bind>` := q){
		binding[entity] = bind;
	}
	
	al = binding[ent];
	
	for(/(Expr) `<VId vid>.<Id id>` := q){
		if(vid := al && id := attr)
			return true;
	}
	
	return false;
}

bool explicit_use(Statement s, _ , Id attr){
	
	visit(s){
		case attr: return true;
	}
	
	return false;
}



bool use_entity(EvoQuery q, EId entity) = size([ e | /EId e := q, e := entity]) > 0;
