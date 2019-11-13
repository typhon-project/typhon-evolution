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

EvoQuery entity_rename(EvoQuery q, EId old_name, EId new_name){
	
	req = visit(q){
		case old_name => new_name
	};
	
	return req;
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

	return q;
}

EvoQuery entity_merge(EvoQuery q,  EId new_name, EId entity1, EId entity2){

	map[EId, VId] binding = ();
	
	for(/(Binding) `<EId entity> <VId bind>` := q){
		binding[entity] = bind;
	}
	
	if(entity1 in binding && entity2 in binding){
		replace = binding[entity2];
		
		q = visit(q){
			case replace => binding[entity1]
				
		}
	
		return parse(#EvoQuery, "#@ Merge operation not yet supported for this case @# <q>");
	}
	else{
		q = entity_rename(q, entity1, new_name);
		q = entity_rename(q, entity2, new_name);
	}
		
	return q;
}


