module lang::typhonevo::EvoCompiler

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::EntityEvolution;


EvoSyntax evolve(EvoSyntax x){
	operators = extract_op(x);
	
	for ( ChangeOperator op <- operators){	
		x = visit(x){
			case EvoQuery q => transform(q, op)
		};
	};
	
	return x;
}

EvoQuery transform(EvoQuery evoq, ChangeOperator op){
	// Ignoring the query with annotation
	
	switch(evoq){
		case (EvoQuery)`<Annotation annot>  <Query query>`:{
			return evoq;
		}
	};
	
	visit(op){
		case EntityOperation operation: {
			evoq = evolve_entity(evoq, operation);
		}
	};
	
	return evoq;
}



list[ChangeOperator] extract_op(EvoSyntax x) {
	l = [];

	visit(x){
		case ChangeOperator c: l = l + [c];
	};
	
	return l;
}

