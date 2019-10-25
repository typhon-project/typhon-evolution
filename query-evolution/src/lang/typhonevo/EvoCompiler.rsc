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
	
	switch(op){
		case (ChangeOperator) `Entity  <Operation operation>`: {
			evoq = evolve_entity(evoq, operation);
		}
	};
	
	return evoq;
}


EvoQuery evolve_entity(EvoQuery q, Operation op){
	switch(op){
		case (Operation) `Rename  <EId old_id> to <EId new_id>`:{
		 	return entity_rename(q, old_id, new_id);
		}
		case (Operation) `Remove  <EId entity>`: {
			return entity_remove(q, entity);
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



list[ChangeOperator] extract_op(EvoSyntax x) {
	l = [];

	visit(x){
		case ChangeOperator c: l = l + [c];
	};
	
	return l;
}

