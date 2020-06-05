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

EvoQuery attribute_rename(EvoQuery q, str entity, str old_name, str new_name, Schema s){

	e = parse(#EId, entity);
	old = parse(#Id, old_name);
	new = parse(#Id, new_name);
	
	if(!use_entity(q, eid)){
		return q;
	}
	
	for(/(Binding) `<EId found_e> <VId bind>` := q){
		if(found_e := e){
			println("found");
		
			EvoQuery res = visit(q){
				case (Expr) `<VId v>.<Id c>` => (Expr) `<VId v>.<Id new>`
				when c := old && v := bind
				case (Expr) `<VId v>.<Id c>.<{Id"."}+ r>` => (Expr) `<VId v>.<Id new>.<{Id"."}+ r>`
				when c := old && v := bind
				case (KeyVal) `<Id c> : <Expr e>` => (KeyVal) `<Id new> : <Expr e>`
				when c := old
			};
			
			if(res := q){
				return q;
			}
			
			res = setStatusChanged(res);
			return res;
		}
	}
	
	return q;
}

EvoQuery attribute_remove(EvoQuery q, str entity, str n, Schema s){

	eid = parse(#EId, entity);
	name = parse(#Id, n);
	
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


EvoQuery attribute_add(EvoQuery q, str attribute, str ent){

	attr = parse(#Id, attribute);
	entity = parse(#EId, ent);

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
