module lang::typhonevo::utils::SchemaUtils

import lang::typhonml::Util;
import List;

Rels get_relations(Schema s, str entity1, str entity2)
	= toSet([ r |  Rel r <- s.rels, r.from == entity1, r.to == entity2] + 
			[ r |  Rel r <- s.rels, r.to == entity1, r.from == entity2]);

	

Rel get_relation_by_name(Schema s, str name){
	res = [r | Rel r <- s.rels, r.fromRole == name];
	
	if(isEmpty(res)){
		return head([r | Rel r <- s.rels, r.toRole == name]);
	}
	
	return head(res);
}