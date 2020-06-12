module lang::typhonevo::handlers::RelationEvolution

import IO;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonml::Util;


EvoQuery rename_relation(EvoQuery q, str entity, str old_name, str new_name){
	
	old = parse(#Id, old_name);
	new = parse(#Id, new_name);
	e = parse(#EId, entity);
		
	for(/(Binding) `<EId found_e> <VId bind>` := q){
		if(found_e := e){
			println("found");
		
			EvoQuery res = visit(q){
				case (Expr) `<VId v>.<Id c>` => (Expr) `<VId v>.<Id new>`
				when c := old && v := bind
				case (Expr) `<VId v>.<Id c>.<{Id"."}+ r>` => (Expr) `<VId v>.<Id new>.<{Id"."}+ r>`
				when c := old && v := bind
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


EvoQuery remove_relation(EvoQuery q, str entity, str to_rm){
	
	to_remove = parse(#Id, to_rm);
	
	if(query_use_relation(q, to_remove)){
		q = setStatusBroken(q, "The relation <to_remove> was removed");
	}
	
	return q;
}


EvoQuery change_containment(EvoQuery q, str rela, str containment){
	
	relation = parse(#Id, rela);
	
	if(query_use_relation(q, relation)){
		q = setStatusChanged(q);
	}
	
	return q;
}


EvoQuery change_cardinality(EvoQuery q, Id rela, str card){
	
	c = parse(#Cardinality, card);
	relation = parse(#Id, rela);
	
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

