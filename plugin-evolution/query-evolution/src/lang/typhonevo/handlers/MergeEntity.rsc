module lang::typhonevo::handlers::MergeEntity

import IO;
import ParseTree;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonql::Expr;
import lang::typhonevo::utils::QueryManipulation;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonevo::utils::SchemaUtils;
import lang::typhonevo::utils::Util;
import String;
import lang::typhonml::Util;

EvoQuery entity_merge(EvoQuery q,  str relation, str entity1, str entity2, Schema s){

	if(contains(relation, ".")){
		splitted = split(".", relation);
		rel_entity = splitted[0];
		relation = splitted[1];
		
		if(rel_entity != entity1){
			tmp = entity2;
			entity2 = entity1;
			entity1 = tmp;
		}
	}

	e1 = parse(#EId, entity1);
	e2 = parse(#EId, entity2);
	rela = parse(#Id, relation);
	
	// Filtering the unrelevant queries
	
	relevant = false;
	
	for(/(EId) `<EId id>` := q){
		if(id == e1 || id == e2)
			relevant = true;
	}
	
	if(!relevant)
		return q;

	switch(q.q.query){
		case (Statement) `insert <{Obj ","}* obj>` : 
			return handle_merge_insert(q, e1, e2, rela, s);
		case (Statement) `delete <Binding binding> <Where? where>` : 
			return handle_merge_delete(q, e1, e2, rela, s);
		case (Statement) `update <Binding binding> <Where? where> set  { <{KeyVal ","}* keyVals> }` : 
			return handle_merge_update(q, e1, e2, rela, s);
		case Query query : 
			return handle_merge_query(q, e1, e2, rela, s);
	};
	
	return q;
}


EvoQuery handle_merge_insert(EvoQuery q, EId e1, EId e2, Id relation, Schema s){
	
	for(/(EId) `<EId id>` := q){
		if(id == e1)
			q = setStatusBroken(q, "Entities <e1> and <e2> merged. The insert query should be modified to handle the fields of <e2>");
		if(id == e2)
			q = setStatusBroken(q, "Entity <e2> no longuer exists. It was merged into <e1>");
	}

	return q;
}

EvoQuery handle_merge_query(EvoQuery q, EId e1, EId e2, Id relation, Schema s){
	
	// Get the bindings for the query
	bindings = get_bindings(q);
	
	// CASE 1 : Both entities are in the query
	if(e1 in bindings && e2 in bindings){
		
		e2_alias = bindings[e2];
		e1_alias = bindings[e1];
		
		binding_to_delete = (Binding) `<EId e2> <VId e2_alias>`;
		q = removeBinding(q, binding_to_delete); 
		
		// Renaming the reference to e2 alias by the e1 alias
		q = visit(q){
			case e2_alias => e1_alias	
		}
		
		// remove duplicate reference for the alias in from
		q = visit(q){
			case (Query) `from <{Binding ","}+ bindings> select <Result result>, <Result result> <Where? where> <Agg* a1>`
				=> (Query) `from <{Binding ","}+ bindings> select <Result result> <Where? where> <Agg* a1>`
		}
		
		q = removeExprFromWhere(q, relation);
		
		q = setStatusChanged(q, "Entities <e1> and <e2> merged. The query might return a different Query Set");
		
		return q;
	}
	
	// Case E2 in bindings
	if(e2 in bindings){
		q = setStatusBroken(q, "Entity <e2> merged with <e1>. The fields of <e2> are now in <e1>");
		return q;
	}
	
	// Case E1 in bindings
	if(e1 in bindings){
		e1_alias = bindings[e1];
		
		select_e1 = false;
		
		visit(q.q.query.selected){
			case e1_alias: select_e1 = true;
		}
		
		if(select_e1)
			q = setStatusWarn(q, "Entity <e2> merged with <e1>. The field of <e2> are now in <e1> thus, the Query Set returned by the query might have changed");
			
		return q;
	} 
	
	return q;
}

EvoQuery handle_merge_update(EvoQuery q, EId e1, EId e2, Id relation, Schema s){
	
	for(/(EId) `<EId id>` := q){
		if(id == e2)
			q = setStatusBroken(q, "Entity <e2> no longuer exists. It was merged into <e1>");
	}
	
	return q;
}

EvoQuery handle_merge_delete(EvoQuery q, EId e1, EId e2, Id relation, Schema s){

	for(/(EId) `<EId id>` := q){
		if(id == e1)
			q = setStatusWarn(q, "Entity <e2> merged into <e1>. The delete statement will also remove fields of <e2>");
		if(id == e2)
			q = setStatusBroken(q, "Entity <e2> no longuer exists. It was merged into <e1>");
	}

	return q;
}
