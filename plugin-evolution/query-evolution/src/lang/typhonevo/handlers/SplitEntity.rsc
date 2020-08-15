module lang::typhonevo::handlers::SplitEntity

import IO;
import List;
import Set;
import ParseTree;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonql::Expr;
import lang::typhonevo::utils::QueryManipulation;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonevo::utils::SchemaUtils;
import lang::typhonevo::utils::Util;
import lang::typhonml::TyphonML;

import lang::typhonml::Util;

EvoQuery split_vertical(EvoQuery q,  str entity, str new_entity, str attr, Schema s){
	
	e1 = parse(#EId, entity);
	e2 = parse(#EId, new_entity);
	
	println(attr);
	
	switch(q.q.query){
		case (Statement) `insert <{Obj ","}* obj>` : 
			return setStatusBroken(q, "Entity <e1> split into <e1>, <e2>. Two insert queries are probably needed.");
		case (Statement) `delete <Binding binding> <Where? where>` : 
			return setStatusBroken(q,"Entity <e1> split into <e1>, <e2>. Two delete queries are probably needed.");
		case (Statement) `update <Binding binding> <Where? where> set  { <{KeyVal ","}* keyVals> }` : 
			return setStatusBroken(q, "Entity <e1> split into <e1>, <e2>. The update cannot be automatically adapted.");
		case Query query : 
			return handle_split_query(q, e1, e2, s);
	};
}

EvoQuery handle_split_query(EvoQuery q, EId e1, EId e2, Schema s){
	return setStatusWarn(q, "NOT IMPLEMENTED");
}
