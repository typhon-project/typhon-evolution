module lang::typhonevo::utils::EvolveStatus

import lang::typhonevo::EvoAbstractSyntax;
import ParseTree;

EvoQuery setStatusError(EvoQuery q){
	Status s = (Status) `ERR`;
	return set_status(s, q);
}

EvoQuery setStatusError(EvoQuery q, str txt){
	Status s = (Status) `ERR`;
	q = set_status(s,q);
	return annotate(txt, q);
}

EvoQuery setStatusWarn(EvoQuery q){
	Status s = (Status) `WARN`;
	return set_status(s, q);
}

EvoQuery setStatusWarn(EvoQuery q, str txt){
	Status s = (Status) `WARN`;
	q = set_status(s,q);
	return annotate(txt, q);
}


EvoQuery setStatusChanged(EvoQuery q){
	Status s = (Status) `MOD`;
	return set_status(s, q);
}

EvoQuery setStatusChanged(EvoQuery q, str txt){
	Status s = (Status) `MOD`;
	q = set_status(s,q);
	return annotate(txt, q);
}

EvoQuery set_status(Status s, (EvoQuery)`<QlQuery q>`)
	= (EvoQuery) `<Status s> <QlQuery q>`;

EvoQuery set_status(Status s, (EvoQuery)`<Status old>  <QlQuery q>`){
	Status final = status_priority(s, old);
	return (EvoQuery) `<Status final> <QlQuery q>`;
}

EvoQuery set_status(Status s, (EvoQuery)`<Status old> <Annotation+ a> <QlQuery q>`){
	Status final = status_priority(s, old);
	return (EvoQuery) `<Status final> <Annotation+ a> <QlQuery q>`;
}

EvoQuery annotate(str text, EvoQuery q){
	Annotation a = parse(#Annotation, "#@ <text> @#");
	return set_annotation(a, q);
}

EvoQuery set_annotation(Annotation a, (EvoQuery)`<Status s>  <QlQuery q>`)
	= (EvoQuery) `<Status s> <Annotation a> <QlQuery q>`;

EvoQuery set_annotation(Annotation a, (EvoQuery)`<Status s> <Annotation+ old> <QlQuery q>`)
	= (EvoQuery) `<Status s> <Annotation+ old> <Annotation a> <QlQuery q>`;


Status status_priority((Status) `MOD`, s:(Status) `WARN`) = s;
Status status_priority((Status) `MOD`, s:(Status) `ERR`) = s;
Status status_priority((Status) `WARN`, s:(Status) `ERR`) = s;
default Status status_priority(Status new, _) = new;
