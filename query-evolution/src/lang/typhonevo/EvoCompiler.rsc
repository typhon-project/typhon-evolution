module lang::typhonevo::EvoCompiler

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;


EvoSyntax test_modif(EvoSyntax x){
	operators = extract_op(x);
	
	for ( ChangeOperator op <- operators){		
		x = visit(x){
			case Query q => transform(q, op)
		};
	};
	
	return x;
}

Query transform(Query q, ChangeOperator op){
	
	switch(op){
		case (ChangeOperator) `Entity  <Operation operation>`: {
			q = evolve_entity(q, operation);
		}
	};
	
	return q;
}


Query evolve_entity(Query q, Operation op){
	switch(op){
		case (Operation) `Rename  <EId old_id> to <EId new_id>`:{
		 		return entity_rename(q, old_id, new_id);
		}
	};
	
	return q;
}


Query entity_rename(Query q, EId old_name, EId new_name){
	
	req = visit(q){
		case old_name => new_name
	};
	
	return req;
}


list[ChangeOperator] extract_op(EvoSyntax x) {
	l = [];

	visit(x){
		case ChangeOperator c: l = l + [c];
	};
	
	return l;
}

