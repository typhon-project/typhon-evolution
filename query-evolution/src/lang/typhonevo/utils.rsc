module lang::typhonevo::utils

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

	
