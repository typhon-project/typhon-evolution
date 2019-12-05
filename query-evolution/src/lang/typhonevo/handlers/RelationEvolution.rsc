module lang::typhonevo::handlers::RelationEvolution

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonml::Util;

// Handler for rename
EvoQuery evolve_relation(EvoQuery q, (RelationOperations) `rename relation <Id old_name> as <Id new_name>`, Schema s){
	println("progress");
	EvoQuery res = visit(q){
		case (Expr) `<VId v>.<Id c>` => (Expr) `<VId v>.<Id new_name>`
		when c := old_name
	};
	
	if(res := q){
		return q;
	}
	
	res = setStatusChanged(res);
	
	return res;
}

default EvoQuery evolve_relation(EvoQuery q, _, _){
	println("nothing relation");
	return q;
}

