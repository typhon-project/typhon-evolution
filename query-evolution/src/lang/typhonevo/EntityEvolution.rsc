module lang::typhonevo::EntityEvolution

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonql::Expr;

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
			return entity_merge(q, new_name, entity1, entity2);
		}
		
	};
	
	return q;
}

default EvoQuery evolve(EvoQuery q, EntityOperation op) = q;

EvoQuery entity_rename(EvoQuery q, EId old_name, EId new_name){
	
	return visit(q){
		case old_name => new_name
	};
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
		return parse(#EvoQuery, "#@ Entity <name> removed. That query is broken @# <query>");
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
		
		println("<e1_vid>.to_<entity2> == <e2_vid>");
		Expr join_expr = parse(#Expr, "<e1_vid>.to_<entity2> == <e2_vid>");
		// Transform
		
		q = visit(q){
		
			case (Query) `from <Binding bind>, <{Binding ","}+ end> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`: {
					if(bind == old_bind){
						insert (Query) `from <Binding e1_bind>, <Binding e2_bind>, <{Binding ","}+ end> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`;
					}
				}
				
			case (Query) `from <{Binding ","}+ before>, <Binding bind>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`:{
					if(bind == old_bind){
						insert (Query) `from <{Binding ","}+ before>, <Binding e1_bind>, <Binding e2_bind>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`;
					}
				}
			
			case (Query) `from <{Binding ","}+ front>, <Binding bind> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`:{
					
					if(bind == old_bind){
						insert (Query) `from <{Binding ","}+ front>, <Binding e1_bind>, <Binding e2_bind> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`;
					}
				}
				
			case (Query) `from <Binding bind> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`: {
					if(bind == bind){
						insert (Query) `from <Binding e1_bind>, <Binding e2_bind> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`;
					}
				}
		}
		
		// Second pass to rename alias and add where clause
		q = visit(q){
			case old_alias => e1_vid
			
			case (Where) `where  <{Expr ","}+ clause>`
				=> (Where) `where  <{Expr ","}+ clause>, <Expr join_expr>`
		}
	}
	
	return q;
}

EvoQuery entity_merge(EvoQuery q,  EId new_name, EId entity1, EId entity2){

	map[EId, VId] binding = ();
	
	for(/(Binding) `<EId entity> <VId bind>` := q){
		binding[entity] = bind;
	}
	
	if(entity1 in binding && entity2 in binding){
		old_alias = binding[entity2];
		del_binding = (Binding) `<EId entity2> <VId old_alias>`;
		
		new_alias = binding[entity1];
		result = (Result) `<Expr new_alias>`;
		
		
		// alter Binding
		q = visit(q){
			case old_alias => new_alias
			
			case (Query) `from <{Binding ","}+ before>, <Binding del_binding>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
				=> (Query) `from <{Binding ","}+ before>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
			
			case (Query) `from <{Binding ","}+ front>, <Binding del_binding> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
				=> (Query) `from <{Binding ","}+ front> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
				
			case (Query) `from <Binding del_binding>, <{Binding ","}+ end> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
				=> (Query) `from <{Binding ","}+ end> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
				
		}
		
		// alter Results

		q = visit(q){
			case (Query) `from <{Binding ","}+ bindings> select <Result result>, <Result result> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
				=> (Query) `from <{Binding ","}+ bindings> select <Result result> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
		}
		
		q = entity_rename(q, entity1, new_name);
		
		// Clear where clause

	
		return q;
	}
	else{
		q = entity_rename(q, entity1, new_name);
		q = entity_rename(q, entity2, new_name);
	}
		
	return q;
}


