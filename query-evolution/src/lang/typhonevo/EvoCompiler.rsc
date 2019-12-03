module lang::typhonevo::EvoCompiler

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::EntityEvolution;
import lang::typhonevo::AttributeEvolution;
import lang::typhonml::XMIReader;
import lang::typhonml::Util;


EvoSyntax evolve(EvoSyntax x, loc location){
	operators = extract_op(x);
	
	str xmi = readFile(location + "<extract_path(x)>");
	Schema s = loadSchemaFromXMI(xmi);
	
	for ( ChangeOperator op <- operators){	
		x = visit(x){
			case EvoQuery q => transform(q, op)
		};
	};
	
	return x;
}

EvoQuery transform(q:(EvoQuery)`<Annotation _>  <Query _>`, _) = q;


EvoQuery transform(EvoQuery evoq, ChangeOperator op){
	// Ignoring the query with annotation
	
	if ((EvoQuery)`<Annotation annot>  <Query query>` := evoq) {
		return evoq;
	}
	
	// TODO remove this (pattern match in signature
	visit(op){
		case EntityOperation operation: {
			evoq = evolve_entity(evoq, operation);
		}
		case AttributesOperations operation:{
			evoq = evolve_attribute(evoq, operation);
		}
		
	};
	
	return evoq;
}

list[ChangeOperator] extract_op(EvoSyntax x) = [ c | /ChangeOperator c := x];
Path extract_path(EvoSyntax x) = [c | /Path c := x][0];


