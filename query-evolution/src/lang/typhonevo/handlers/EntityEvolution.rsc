module lang::typhonevo::handlers::EntityEvolution

import IO;
import ParseTree;
import List;
import Exception;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonql::Expr;
import lang::typhonevo::utils::QueryManipulation;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonevo::utils::SchemaUtils;
import lang::typhonml::Util;

// DISPATCHER
	
EvoQuery evolve_entity(EvoQuery q, (EntityOperation) `split entity <EId name> { left <EId entity1> right <EId entity2> }`, Schema s)
	= entity_split(q, name, entity1, entity2);
	
	
EvoQuery evolve_entity(EvoQuery q, (EntityOperation)  `split entity vertical <EId entity1> to <EId entity2> attributes : [ <{Expr ","}+ expr> ]`, Schema s)
	= entity_split(q, entity1, entity1, entity2);

default EvoQuery evolve_entity(EvoQuery q, _, _) = q;


// HANDLERS

EvoQuery entity_rename(EvoQuery q, EId old_name, EId new_name){
	
	EvoQuery e = visit(q){
		case old_name => new_name
	};
	
	if (e := q)
		return q;
	
	return setStatusChanged(e);
}


EvoQuery entity_remove(EvoQuery q, EId name){
	
	QlQuery query;
	matched = false;
	
	visit(q){
		case QlQuery qu:{
			query = qu;
		}
		case name :{
			matched = true;
		}
	}
	
	if(matched){
		q = setStatusBroken(q, "Entity <name> removed. This query is broken");
	}
	return q;
}

EvoQuery entity_split(EvoQuery q, EId old_name, EId entity1, EId entity2){
	
	map[EId, VId] binding = ();
	
	for(/(Binding) `<EId entity> <VId bind>` := q){
		binding[entity] = bind;
	}
	
	if(old_name in binding){
		// Preparing all the data 
		VId old_alias = binding[old_name];
		VId e1_vid = parse(#VId, "<old_alias>1");
		VId e2_vid = parse(#VId, "<old_alias>2");
		
		Binding e1_bind = (Binding) `<EId entity1> <VId e1_vid>`;
		Binding e2_bind = (Binding) `<EId entity2> <VId e2_vid>`;
		
		Binding old_bind = (Binding) `<EId old_name> <VId old_alias>`;
		
		Expr join_expr = parse(#Expr, "<e1_vid>.to_<entity2> == <e2_vid>");
		// Transform
		
		q = visit(q){
		
			case (Query) `from <Binding bind>, <{Binding ","}+ end> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`: {
					if(bind == old_bind){
						insert (Query) `from <Binding e1_bind>, <Binding e2_bind>, <{Binding ","}+ end> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`;
					}
					else{
						fail;
					}
				}
				
			case (Query) `from <{Binding ","}+ before>, <Binding bind>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`:{
					if(bind == old_bind){
						insert (Query) `from <{Binding ","}+ before>, <Binding e1_bind>, <Binding e2_bind>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`;
					}
					else{
						fail;
					}
				}
			
			case (Query) `from <{Binding ","}+ front>, <Binding bind> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`:{
					
					if(bind == old_bind){
						insert (Query) `from <{Binding ","}+ front>, <Binding e1_bind>, <Binding e2_bind> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`;
					}
					else{
						fail;
					}
				}
				
			case (Query) `from <Binding bind> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`: {
					if(bind == bind){
						insert (Query) `from <Binding e1_bind>, <Binding e2_bind> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`;
					}
					else{
						fail;
					}
				}
		}
		
		// Second pass to rename alias and add where clause
		q = visit(q){
			case old_alias => e1_vid
			
			case (Where) `where  <{Expr ","}+ clause>`
				=> (Where) `where  <{Expr ","}+ clause>, <Expr join_expr>`
		}
		q = setStatusWarn(q, "Entity <old_name> split into <entity1>, <entity2>");
	}
	
	return q;
}

EvoQuery entity_merge(EvoQuery q,  EId new_name, EId entity1, EId entity2, Schema s){

	map[EId, VId] binding = ();
	
	for(/(Binding) `<EId entity> <VId bind>` := q){
		binding[entity] = bind;
	}
	
	if(entity1 in binding && entity2 in binding){
	
		old_alias = binding[entity2];
		del_binding = (Binding) `<EId entity2> <VId old_alias>`;
		
		new_alias = binding[entity1];
		result = (Result) `<Expr new_alias>`;
		
		q = removeBinding(q, del_binding); 
		
		
		// alter alias
		q = visit(q){
			case old_alias => new_alias	
		}
		
		// alter Results

		q = visit(q){
			case (Query) `from <{Binding ","}+ bindings> select <Result result>, <Result result> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
				=> (Query) `from <{Binding ","}+ bindings> select <Result result> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
		}
		
		q = entity_rename(q, entity1, new_name);
		
		for(Rel r <- get_relations(s, "<entity1>", "<entity2>")){
			
			try {
				Id from = parse(#Id, r.fromRole);
				q = removeExprFromWhere(q, from);
				
				Id to  = parse(#Id, r.toRole);
				q = removeExprFromWhere(q, to);
			}
			catch: a = 10;
		}
		
		switch(q.q.query){
			case (Statement) `insert <{Obj ","}* obj>` : 
				q = setStatusBroken(q, "<entity1> and <entity2> merged.");
			case (Statement) `delete <Binding binding> <Where? where>` : 
				q = setStatusWarn(q, "<entity1> and <entity2> merged. Delete will erase more information than before");
			case (Statement) `update <Binding binding> <Where? where> set  { <{KeyVal ","}* keyVals> }` : 
				q = setStatusChanged(q, "<entity1> and <entity2> merged.");
			case Query quer : 
				q = setStatusWarn(q, "Query return a different QuerySet : <new_name> contains attributes from <entity1> and <entity2>");
		};
		
	}
	else{
		r1 = entity_rename(q, entity1, new_name);
		r2= entity_rename(r1, entity2, new_name);
		
		if(r2 := q)
			return q;
			
		q = r2;
		switch(q.q.query){
			case (Statement) `insert <{Obj ","}* obj>` : 
				q = setStatusBroken(q, "<entity1> and <entity2> merged.");
			case (Statement) `delete <Binding binding> <Where? where>` : 
				q = setStatusWarn(q, "<entity1> and <entity2> merged. Delete will erase more information than before");
			case (Statement) `update <Binding binding> <Where? where> set  { <{KeyVal ","}* keyVals> }` : 
				q = setStatusChanged(q, "<entity1> and <entity2> merged.");
			case Query quer : 
				q = setStatusWarn(q, "Query return a different QuerySet : <new_name> contains attributes from <entity1> and <entity2>");
		};
		
	}
		
	return q;
}


EvoQuery entity_migration(EvoQuery q, EId entity_name){
	return entity_rename(q, entity_name, parse(#EId, "<entity_name>_migrated"));
}

