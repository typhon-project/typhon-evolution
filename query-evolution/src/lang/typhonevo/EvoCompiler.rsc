module lang::typhonevo::EvoCompiler

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;


EvoSyntax test_modif(EvoSyntax x){
	operators = extract_op(x);
	
	for ( ChangeOperator op <- operators){	
		x = visit(x){
			case EvoQuery q => transform(q, op)
		};
	};
	
	return x;
}

EvoQuery transform(EvoQuery evoq, ChangeOperator op){
	// Ignoring the query with annotation
	
	switch(evoq){
		case (EvoQuery)`<Annotation annot>  <Query query>`:{
			return evoq;
		}
	};
	
	visit(op){
		case EntityOperation operation: {
			evoq = evolve_entity(evoq, operation);
		}
	};
	
	return evoq;
}


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
			return entity_split(q, new_name, entity1, entity2);
		}
		
	};
	
	return q;
}

// ENTITY

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

	return q;
}



list[ChangeOperator] extract_op(EvoSyntax x) {
	l = [];

	visit(x){
		case ChangeOperator c: l = l + [c];
	};
	
	return l;
}

