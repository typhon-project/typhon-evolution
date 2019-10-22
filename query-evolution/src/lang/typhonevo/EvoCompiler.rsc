module lang::typhonevo::EvoCompiler

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;


EvoSyntax test_modif(EvoSyntax x){
	op = extract_op(x);
	queries = extract_queries(x);
	
	println(size(op));
	println(size(queries));
	
	return x;
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

