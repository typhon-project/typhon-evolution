module lang::typhonevo::EvoCompiler

import IO;
import ParseTree;
import Exception;
import List;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::handlers::EntityEvolution;
import lang::typhonevo::handlers::AttributeEvolution;
import lang::typhonevo::handlers::RelationEvolution;

import lang::typhonml::XMIReader;
import lang::typhonml::Util;
import lang::typhonml::TyphonML;

import lang::typhonevo::utils::SchemaUtils;
import lang::typhonevo::utils::EvolveStatus;


EvoSyntax evolve(EvoSyntax x, loc location){
	operators = extract_op(x);
	
	try{
		str xmi = readFile(location + "<extract_path(x)>");
		Model m = xmiString2Model(xmi);
		Schema s = model2schema(m);
	}
	catch: s = Schema();
		
	// Init all queries to unchanged
	x = visit(x){
		case EvoQuery q => setStatusUnchanged(q)
	};

	for ( EvoChangeOp op <- operators){	
		x = visit(x){
			case EvoQuery q => transform(q, op.op, s)
		};
	};
	
	return x;
}

EvoQuery transform(q:(EvoQuery)`BROKEN  <QlQuery _>`, _, _) = q;
EvoQuery transform(q:(EvoQuery)`BROKEN <Annotation+ _>  <QlQuery _>`, _, _) = q;

EvoQuery transform(EvoQuery evoq, EntityOperation op, Schema s) = evolve_entity(evoq, op, s);
EvoQuery transform(EvoQuery evoq, AttributesOperations op, Schema s) = evolve_attribute(evoq, op, s);
EvoQuery transform(EvoQuery evoq, RelationOperations op, Schema s) = evolve_relation(evoq, op, s);


list[EvoChangeOp] extract_op(EvoSyntax x) = [ c | /EvoChangeOp c := x];
list[EvoQuery] extract_queries(EvoSyntax x) = [ c | /EvoQuery c := x];

Path extract_path(EvoSyntax x) = [c | /Path c := x][0];


