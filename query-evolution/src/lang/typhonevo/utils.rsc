module lang::typhonevo::utils

import lang::typhonevo::EvoAbstractSyntax;

Query removeBinding(Query q, Binding b)
	= visit(q){
		case (Query) `from <{Binding ","}+ before>, <Binding del_binding>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
			=> (Query) `from <{Binding ","}+ before>, <{Binding ","}+ after> select <{Result ","}+ selected> <Where? where> <GroupBy? groupBy> <OrderBy? orderBy>`
		when b := del_binding
	};
