module lang::typhonevo::utils::QueryManipulation

import lang::typhonevo::EvoAbstractSyntax;

EvoQuery removeBinding(EvoQuery q, Binding del_binding)
	= visit(q){
			case (Query) `from <{Binding ","}+ before>, <Binding b>, <{Binding ","}+ after> select <{Result ","}+ s1> <Where? w1> <GroupBy? g1> <OrderBy? o1>`
				=> (Query) `from <{Binding ","}+ before>, <{Binding ","}+ after> select <{Result ","}+ s1> <Where? w1> <GroupBy? g1> <OrderBy? o1>`
			when b := del_binding
			
			case (Query) `from <Binding b>, <{Binding ","}+ end> select <{Result ","}+ s2> <Where? w2> <GroupBy? g2> <OrderBy? o2>`
				=> (Query) `from <{Binding ","}+ end> select <{Result ","}+ s2> <Where? w2> <GroupBy? g2> <OrderBy? o2>`
			when b := del_binding
			
			case (Query) `from <{Binding ","}+ front>, <Binding b> select <{Result ","}+ s3> <Where? w3> <GroupBy? g3> <OrderBy? o3>`
				=> (Query) `from <{Binding ","}+ front> select <{Result ","}+ s3> <Where? w3> <GroupBy? g3> <OrderBy? o3>`
			when b := del_binding
		};


EvoQuery addBinding(EvoQuery q, Binding b)
	= visit(q){
		case (Query) `from <{Binding ","}+ bindings> select <{Result ","}+ s1> <Where? w1> <GroupBy? g1> <OrderBy? o1>`
			=> (Query) `from <{Binding ","}+ bindings>, <Binding b> select <{Result ","}+ s1> <Where? w1> <GroupBy? g1> <OrderBy? o1>`
	};
	


EvoQuery removeExprFromWhere(EvoQuery q, Id relation)
	= visit(q){
		case (Where) `where <{Expr ","}+ front>, <VId v>.<Id c> == <Expr a>, <{Expr ","}* end>`
			=> (Where) `where <{Expr ","}+ front>, <{Expr ","}* end>`
		when c := relation
		
		case (Where) `where <{Expr ","}* before>, <VId v>.<Id c> == <Expr a>, <{Expr ","}+ after>`
			=> (Where) `where <{Expr ","}* before>, <{Expr ","}+ after>`
		when c := relation
		
		case (Where) `where <VId v>.<Id c> == <Expr a>`
			=> (Where) `where true == true`
		when c := relation
	};
	

map[EId, VId] get_bindings(EvoQuery q){
	map[EId, VId] binding = ();

	for(/(Binding) `<EId entity> <VId bind>` := q){
		binding[entity] = bind;
	}
	
	return binding;
} 
	