module lang::typhonevo::EvoCompiler

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;


EvoSyntax test_modif(EvoSyntax x){
	operators = extract_op(x);
	queries = extract_queries(x);
	
	for ( ChangeOperator op <- operators){
		println(op);
		for (Query q <- queries){
			transform(q, op);
		}
	};
	
	return x;
}

Query transform(Query q, ChangeOperator op){
	
	switch(op){
		case (ChangeOperator) `Entity  <Operation operation>` => {
			evolve_entity(q, operation);
			println("End handling entity op");
		}
	};
	
}


Query evolve_entity(Query q, Operation op){
	switch(op){
		case (Operation) `Add  <EId id>` => {
				println("Nothing to do");
				q;
		}
		case (Operation) `Rename  <EId old_id> to <EId new_id>` => entity_rename(q, old_id, new_id)
	};
}


Query entity_rename(Query q, EId old_name, EId new_name){
	
	req = visit(q){
		case old_name => new_name
	};
	
	println("<req>");
	
	return req;
}





list[ChangeOperator] extract_op(EvoSyntax x) {
	l = [];

	visit(x){
		case ChangeOperator c: l = l + [c];
	};
	
	return l;
}


list[Query] extract_queries(EvoSyntax x){
	l = [];
	
	visit(x){
		case Query q: l = l + [q];
	};
	
	return l;
}

