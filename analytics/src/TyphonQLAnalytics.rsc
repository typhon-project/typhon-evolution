module TyphonQLAnalytics

import IO;
import ParseTree;
import lang::typhonql::Expr;
import lang::typhonql::Query;
import lang::typhonql::DML;

syntax QueryType 
	= query: Query query
	| statement: Statement query;
	
public void hello() {
   println("Hello world, this is my first Rascal program");
}

void numberOfEntitiesInBindings(query) {
	x = parse(#QueryType, query);
	extract(x.query);
}

public void extract(Query q) {
	int nbrOfEntities = 0;
	for (Binding binding <- q.bindings){	
			print(binding.entity);
			print(" with alias: "); 
			println(binding.var);
			nbrOfEntities+=1;
	};
	println(nbrOfEntities);
}

public void extract(Statement s) {
	println("Statement parameter");
}