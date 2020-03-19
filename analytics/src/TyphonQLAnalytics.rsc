module TyphonQLAnalytics

import IO;
import lang::typhonql::Query;
import lang::typhonql::DML;
import ParseTree;
import Node;
import Map;
import String;
import Whitespace;
import WhitespaceOrComment;

syntax QueryType
= query: Query query
| statement: Statement query;

data Attribute = attribute(str alias_, list[str] attrs_);
data EntityJoin = entityJoin(str entityName1, list[str] attrs1, str entityName2, list[str] attrs2);
data AttributeComparator = attributeComparator(str entityName, list[str] attributes);
data ImplicitInsert = implicitInsert(str entityName, list[ImplicitInsert] children);

//////////////////////////////////////////
str querytype = "undefined";
str originalQuery = "";
str normalizedQuery = "";
str displayableQuery = "";
map[str, str] mainEntities = ();
list[EntityJoin] joins = [];
list[AttributeComparator] attrComps = [];
list[ImplicitInsert] implicitInserts = [];

//////////////////////////////////////////


void main(s) {
	init();
	originalQuery = s;
	x = parse(#QueryType, s);
	
	extract(x.query);
	
	str temp = normalize(x);
	setNormalizedQuery(temp);
	setDisplayableQuery(temp);
	
	
	printParsingResult();
	
}

void init() {
	querytype = "undefined";
	originalQuery = "";
	normalizedQuery = "";
	displayableQuery = "";
	mainEntities = ();
	joins = [];
	attrComps = [];
	implicitInserts = [];
}

void setNormalizedQuery(str query) {
	normalizedQuery = visit (query) { 
 			 case /[\s]/ => "" 
	};
}

void setDisplayableQuery(str query) {
	displayableQuery= visit (query) { 
			 case /[\n]/ => " "
			 case /[\t]/ => " "
 			
	};
	
	displayableQuery = visit(query) {
		 case /[\s]+/ => " " 
	};
	
}


void printParsingResult() {
	println("Original query: " + originalQuery);
	println("********************************");
	println("Normalized query: " + normalizedQuery);
	println("********************************");
	println("Displayable query: " + displayableQuery);
	println("********************************");
	println("Query type: " + querytype);
	println("********************************");
	println("Main entities:");
	for(entityName <- range(mainEntities))
		println("   - " + entityName);
		
	if(size(joins) > 0) {
		println("********************************");
		println("Joins between entities:");
		for(EntityJoin ej <- joins) {
			str e1 = ej.entityName1;
			str e2 = ej.entityName2;
			println("   - join between " + e1 + toString(ej.attrs1) + " and " + e2  + toString(ej.attrs2)); 
		}
	}
	
	if(size(attrComps) > 0) {
		println("********************************");
		println("Attribute comparisons:");
		for(AttributeComparator ac <- attrComps) {
			println("   - comparison on " + ac.entityName + toString(ac.attributes));
		}
	}
	
	if(size(implicitInserts) > 0) {
		println("********************************");
		println("Implicit inserts:");
		println(implicitInserts);
	}
	
}



public void extract(Query q) {
	querytype = "SELECT";
	for(Binding binding <- q.bindings) {
		str aliasName = "" + binding.var;
		str entityName = "" + binding.entity;
		mainEntities[aliasName] = entityName;
	}
	
	visitWhereClause(q.where);
	visitOrderClause(q.orderBy);
}

public void visitOrderClause(order) {
	visit(order) {
		case OrderBy ob : visitOrderAttributes(ob);
	}

}

public list[str] visitOrderAttributes(OrderBy order) = ["" + attr | attr <- order.attrs];

public void visitWhereClause(where) {
	visit(where) {
		case (Expr) `<Expr e1> == <Expr e2>` : visitAttributeComparatorClause(e1, e2);
		case (Expr) `<Expr e1> != <Expr e2>` : visitAttributeComparatorClause(e1, e2);
		case (Expr) `<Expr e1> \>= <Expr e2>` : visitAttributeComparatorClause(e1, e2);
		case (Expr) `<Expr e1> \<= <Expr e2>` : visitAttributeComparatorClause(e1, e2);
		case (Expr) `<Expr e1> \< <Expr e2>` : visitAttributeComparatorClause(e1, e2);
		case (Expr) `<Expr e1> \> <Expr e2>` : visitAttributeComparatorClause(e1, e2);
		case (Expr) `<Expr e1> in <Expr e2>` : visitAttributeComparatorClause(e1, e2);
		case (Expr) `<Expr e1> like <Expr e2>` : visitAttributeComparatorClause(e1, e2);
	}
}


public str normalize(q) {
	q = visit(q) {
		case (Expr) `<Int _>` => (Expr) `"?"`
		case (Expr) `<Str _>` => (Expr) `"?"`
		case (Expr) `<Real _>` => (Expr) `"?"`
		case (Expr) `<DateTime _>` => (Expr) `"?"`
		case (Expr) `<Bool _>` => (Expr) `"?"`
		case (Expr) `<UUID _>` => (Expr) `"?"`
	}
	
	return "" + q;

}


public void visitAttributeComparatorClause(Expr e1, Expr e2) {

	node part1 = visitExpr(e1);
	node part2 = visitExpr(e2);
	
	bool isAttr1 = isAttribute(part1);
	bool isAttr2 = isAttribute(part2);
	
	str entityName1 = "";
	str entityName2 = "";
	
	if(isAttr1) {
		entityName1 = mainEntities[part1.alias_];
	}
	
	if(isAttr2) {
		entityName2 = mainEntities[part2.alias_];
	}
	
	if(!isEmpty(entityName1) && !isEmpty(entityName2)) {
		list[str] attrs1 = part1.attrs_;
		list[str] attrs2 = part2.attrs_;
		joins += entityJoin(entityName1, attrs1, entityName2, attrs2);
	} else {
		
		if(!isEmpty(entityName1)) {
			list[str] attrs = part1.attrs_;
			if(!isEmpty(attrs)) {
				attrComps += attributeComparator(entityName1, attrs);
			}
			
		} else
			if(!isEmpty(entityName2)) {
				list[str] attrs = part2.attrs_;
				if(!isEmpty(attrs)) {
					attrComps += attributeComparator(entityName2, attrs);
				}
			} 
		
		
	
	}
	
	
	
}

public bool isAttribute(Attribute a) = true;
public bool isAttribute(node a) = false;



public node visitExpr(Expr e) {
	visit(e) {
		case (Expr) `<VId alias_> . <{Id "."}+ attrs_>` : return visitAttributes(alias_, attrs_);
		case (Expr) `<VId alias_> .@id` : return visitAttribute(alias_, "@id");
		case (Expr) `<VId alias_>` : return visitAliasedEntity(alias_);
	}
	
	return e;
}

public Attribute visitAliasedEntity(alias_) {
	return attribute("" + alias_, []);
	
}

public Attribute visitAttribute(alias_, attrs_) {
	str as_ = "" + alias_;
	str attributes = "" + attrs_;
	return attribute(as_, [attributes]);
}

public Attribute visitAttributes(alias_, attrs_) {
	list[str] attrLabels = [];
	for(Id attr <- attrs_) {
		str label = "" + attr;
		attrLabels += label;
	}

	str as_ = "" + alias_;
	return attribute(as_, attrLabels);
}



public void extract(Statement q) {

	visit(q) {
		case (Statement) `insert <{Obj ","}* objects>` : visitInsert(objects);
		case (Statement) `delete <Binding binding> <Where? where>` : visitDelete(binding, where);
		case (Statement) `update <Binding binding> <Where? where> set { <{KeyVal ","}* keyVals> }` : visitUpdate(binding, where, keyVals);
	}

}

public void visitInsert(objects) {
	querytype = "INSERT";
	ImplicitInsert mainInsert = implicitInsert("", []);
	for(Obj obj <- objects) {
		str label = "" + obj.labelOpt;
		str entityName = "" + obj.entity;
		ImplicitInsert ii = visitInsertObject(true, obj, implicitInsert(entityName, []));
		mainInsert.children += [ii];
	}
	
	
	implicitInserts = mainInsert.children;
}

public ImplicitInsert visitInsertObject(bool mainInsert, Obj obj, ImplicitInsert parent) {
	str label = "" + obj.labelOpt;
	str entityName = "" + obj.entity;
	
	if(mainInsert) {
		// alias = entity name
		mainEntities[entityName] = entityName;
		ImplicitInsert ii = visitKeyVals(obj.keyVals, parent);
		parent.children += ii.children;
	} else {
		//implicit insert
		
		ImplicitInsert ii = implicitInsert(entityName, []);
		ii = visitKeyVals(obj.keyVals, ii);
		parent.children += [ii]; 
	}
	
	
	
	return parent;
}

public void visitDelete(binding, where) {
	querytype = "DELETE";
	str aliasName = "" + binding.var;
	str entityName = "" + binding.entity;
	mainEntities[aliasName] = entityName;
	
	visitWhereClause(where);
}

public void visitUpdate(binding, where, keyVals) {
	querytype = "UPDATE";
	
	str aliasName = "" + binding.var;
	str entityName = "" + binding.entity;
	mainEntities[aliasName] = entityName;
	
	visitWhereClause(where);
	implicitInserts = visitKeyVals(keyVals, implicitInsert("", [])).children;
	
}

public ImplicitInsert visitKeyVals(keyVals, ImplicitInsert implicitinsert) {
	
	ImplicitInsert res = implicitInsert(implicitinsert.entityName, implicitinsert.children);
	
	for(KeyVal kv <- keyVals) {
		ImplicitInsert ii = visitKeyVal(kv, implicitinsert);
		res.children += ii.children;
	}
	
	return res;
}

public ImplicitInsert visitKeyVal((KeyVal)`<Id key> : <Expr e>`, ImplicitInsert ii) {
	return visitKeyValExpr(e, ii);
}

public ImplicitInsert visitKeyVal((KeyVal)`@id : <Expr e>`, ImplicitInsert ii) {
	return visitKeyValExpr(e, ii);
}


public ImplicitInsert visitKeyValExpr((Expr) `<Obj obj>`, ImplicitInsert ii) = visitKeyValExpr(obj, ii);
public ImplicitInsert visitKeyValExpr(Obj obj, ImplicitInsert ii) = visitInsertObject(false, obj, ii);

public ImplicitInsert visitKeyValExpr((Expr) `[ <{Obj ","}* entries> ]`, ImplicitInsert ii) {
	ImplicitInsert res = implicitInsert(ii.entityName, ii.children);
	for(Obj obj <- entries) {
		ImplicitInsert ii2 = visitInsertObject(false, obj, ii);
		res.children += ii2.children;
	}
	
	return res;
}

public ImplicitInsert visitKeyValExpr((Expr) `( <Expr e> )`, ImplicitInsert ii) = visitKeyValExpr(e, ii);

public ImplicitInsert visitKeyValExpr(Expr e, ImplicitInsert ii) = ii;


