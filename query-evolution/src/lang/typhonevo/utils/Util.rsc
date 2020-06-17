module lang::typhonevo::utils::Util

import lang::typhonevo::EvoAbstractSyntax;
import IO;

bool QueryUseAttribut(EvoQuery q, EId entity, Id attr){

	// Test if query use entity
	bool res = false;
	
	visit(q){
		case entity: res = true;
	}
	
	if(!res)
		return res;
	
	res = false;
	
	
	// Treating the 2 case : select and DML
	map[EId, VId] binding = ();
	
	for(/(Binding) `<EId entity> <VId bind>` := q){
		binding[entity] = bind;
	}
	
	if(entity in binding){
		VId alia = binding[entity];
		
		visit(q){
			case (Expr) `<VId v>.<Id i>`: {
				if(v := alia && i := attr){
					println(attr);
					res = true;
				}
			}
			case (KeyVal) `<Id i>: <Expr _>`: {
				if(i := attr){
					res = true;
				}
			}
		}
	}
	else{
		visit(q){
			case (KeyVal) `<Id i>: <Expr _>`: {
				if(i := attr){
					res = true;
				}
			}
		}
	}
	
	return res;
} 


map[EId, VId] get_bindings(EvoQuery q){

	map[EId, VId] bindings = ();
	
	for(/(Binding) `<EId entity> <VId bind>` := q){
		bindings[entity] = bind;
	}
	
	return bindings;
}
