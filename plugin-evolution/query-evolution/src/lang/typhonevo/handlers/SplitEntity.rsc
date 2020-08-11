module lang::typhonevo::handlers::SplitEntity

import IO;
import List;
import Set;
import ParseTree;
import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonql::Expr;
import lang::typhonevo::utils::QueryManipulation;
import lang::typhonevo::utils::EvolveStatus;
import lang::typhonevo::utils::SchemaUtils;
import lang::typhonevo::utils::Util;
import lang::typhonml::TyphonML;

import lang::typhonml::Util;

EvoQuery split_vertical(EvoQuery q,  str entity, str new_entity, list[lang::ecore::Refs::Ref[Attribute]] attrs, Schema s){
	println(entity);
	println(new_entity);
	
	return q;
}
