module lang::typhonevo::utils::EvolveStatus

import lang::typhonevo::EvoAbstractSyntax;
import ParseTree;

EvoQuery setStatusError(EvoQuery q){
	Status s = (Status) `ERR`;
	return set_status(s, q);
}

EvoQuery setStatusWarn(EvoQuery q){
	Status s = (Status) `WARN`;
	return set_status(s, q);
}

EvoQuery setStatusChanged(EvoQuery q){
	Status s = (Status) `MOD`;
	return set_status(s, q);
}

EvoQuery set_status(Status s, (EvoQuery)`<Annotation a>  <QlQuery q>`)
	= (EvoQuery) `<Status s> <Annotation a> <QlQuery q>`;

EvoQuery set_status(Status s, (EvoQuery)`<Status _> <Annotation a> <QlQuery q>`)
	= (EvoQuery) `<Status s> <Annotation a> <QlQuery q>`;
	

EvoQuery annotate(str text, EvoQuery q){
	Annotation a = parse(#Annotation, "#@ <text> @#");
	return set_annotation(a, q);
}

EvoQuery set_annotation(Annotation a, (EvoQuery)`<Annotation _>  <QlQuery q>`)
	= (EvoQuery) `<Annotation a> <QlQuery q>`;

EvoQuery set_annotation(Annotation a, (EvoQuery)`<QlQuery q>`)
	= (EvoQuery) `<Annotation a> <QlQuery q>`;
	
EvoQuery set_annotation(Annotation a, (EvoQuery)`<Status s> <Annotation _> <QlQuery q>`)
	= (EvoQuery) `<Status s> <Annotation a> <QlQuery q>`;


