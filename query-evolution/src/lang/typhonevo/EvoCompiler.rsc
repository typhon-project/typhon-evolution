module lang::typhonevo::EvoCompiler

import IO;
import ParseTree;
import List;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::handlers::EntityEvolution;
import lang::typhonevo::handlers::AttributeEvolution;
import lang::typhonml::XMIReader;
import lang::typhonml::Util;
import lang::typhonml::TyphonML;


EvoSyntax evolve(EvoSyntax x, loc location){
	operators = extract_op(x);
	
	str xmi = readFile(location + "<extract_path(x)>");
	Model m = xmiString2Model(xmi);
	Schema s = model2schema(m);

	for ( ChangeOp op <- operators){	
		x = visit(x){
			case EvoQuery q => transform(q, op.op)
		};
	};
	
	return x;
}

EvoQuery transform(q:(EvoQuery)`ERR  <QlQuery _>`, _) = q;
EvoQuery transform(q:(EvoQuery)`ERR <Annotation+ _>  <QlQuery _>`, _) = q;

EvoQuery transform(EvoQuery evoq, EntityOperation op) = evolve_entity(evoq, op);
EvoQuery transform(EvoQuery evoq, AttributesOperations op) = evolve_attribute(evoq, op);


list[ChangeOp] extract_op(EvoSyntax x) = [ c | /ChangeOp c := x];
list[EvoQuery] extract_queries(EvoSyntax x) = [ c | /EvoQuery c := x];

Path extract_path(EvoSyntax x) = [c | /Path c := x][0];


