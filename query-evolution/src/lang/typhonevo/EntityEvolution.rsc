module lang::typhonevo::EntityEvolution

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;

EvoQuery evolve_entity(EvoQuery q, EntityOperation op){
	switch(op){
		case (EntityOperation) `rename entity  <EId old_id> as <EId new_id>`:{
		 	return entity_rename(q, old_id, new_id);
		}
		case (EntityOperation) `remove entity <EId entity>`: {
			return entity_remove(q, entity);
		}
		case (EntityOperation) `split entity <EId name> { left <EId entity1> right <EId entity2> }`: {
			return entity_split(q, name, entity1, entity2);
		}
		case (EntityOperation)  `merge entities <EId entity1> <EId entity2> as <EId new_name>`:{
			return entity_merge(q, new_name, entity1, entity2);
		}
		
	};
	
	return q;
}

default EvoQuery evolve(EvoQuery q, EntityOperation op) = q;

EvoQuery entity_rename(EvoQuery q, EId old_name, EId new_name){
	
	return visit(q){
		case old_name => new_name
	};
}


EvoQuery entity_remove(EvoQuery q, EId name){
	
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
		return parse(#EvoQuery, "#@ Entity <name> removed. That query is broken @# <query>");
	}
	return q;
}

EvoQuery entity_split(EvoQuery q, EId old_name, EId entity1, EId entity2){
	
	map[EId, VId] binding = ();
	
	for(/(Binding) `<EId entity> <VId bind>` := q){
		binding[entity] = bind;
	}
	
	// Change the bindings
	
	// Change the attributes call
	
	// Add join condition
	

	return q;
}

EvoQuery entity_merge(EvoQuery q,  EId new_name, EId entity1, EId entity2){

	map[EId, VId] binding = ();
	
	for(/(Binding) `<EId entity> <VId bind>` := q){
		binding[entity] = bind;
	}
	
	if(entity1 in binding && entity2 in binding){
		old_alias = binding[entity2];
		del_binding = (Binding) `<EId entity2> <VId old_alias>`;
		
		
		// alter Results and Where
		q = visit(q){
			case old_alias => binding[entity1]
			case (Query) `from <{Binding ","}+ before>, <Binding del_binding>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
				=> (Query) `from <{Binding ","}+ before>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
		}
		
		q = entity_rename(q, entity1, new_name);
		
		// Clear where clause

	
		return q;
	}
	else{
		q = entity_rename(q, entity1, new_name);
		q = entity_rename(q, entity2, new_name);
	}
		
	return q;
}


