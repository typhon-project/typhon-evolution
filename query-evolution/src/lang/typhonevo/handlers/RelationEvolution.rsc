module lang::typhonevo::handlers::RelationEvolution

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonml::Util;


EvoQuery evolve_relation(EvoQuery q, (RelationOperations) `change cardinality <Id relation> as <Cardinality c>`, Schema s)
	= change_cardinality(q, relation, c);

default EvoQuery evolve_relation(EvoQuery q, _, _) = q;


EvoQuery rename_relation(EvoQuery q, str entity, Id old_name, Id new_name){

	EvoQuery res = visit(q){
		case (Expr) `<VId v>.<Id c>` => (Expr) `<VId v>.<Id new_name>`
		case (Expr) `<VId v>.<Id c>.<{Id"."}+ r>` => (Expr) `<VId v>.<Id new_name>.<{Id"."}+ r>`
		when c := old_name
	};
	
	if(res := q){
		return q;
	}
	
	res = setStatusChanged(res);
	
	return res;
}


EvoQuery remove_relation(EvoQuery q, str entity, Id to_remove){
	
	if(query_use_relation(q, to_remove)){
		q = setStatusBroken(q, "The relation <to_remove> was removed");
	}
	
	return q;
}


EvoQuery change_containment(EvoQuery q, Id relation, str containment){
	
	if(query_use_relation(q, relation)){
		q = setStatusChanged(q);
	}
	
	return q;
}


EvoQuery change_cardinality(EvoQuery q, Id relation, Cardinality c){
	
	if(query_use_relation(q, relation)){
		q = setStatusWarn(q, "Cardinality of relation <relation> as changed to <c>");
	}
	
	return q;
}



bool query_use_relation(q, relation){
	impacted = false;
	
	visit(q){
		case (Expr) `<VId v>.<Id c>`: {
			if(c := relation)
				impacted = true;
		}
		case (Expr) `<VId v>.<Id c>.<{Id"."}+ r>`: {
			if(c := relation)
				impacted = true;
		}
	};
	
	return impacted;
}

