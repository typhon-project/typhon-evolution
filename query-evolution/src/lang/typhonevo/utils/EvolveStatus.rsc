module lang::typhonevo::utils::EvolveStatus

import lang::typhonevo::EvoAbstractSyntax;
import ParseTree;

EvoQuery setStatusBroken(EvoQuery q){
	Status s = (Status) `BROKEN`;
	return set_status(s, q);
}

EvoQuery setStatusBroken(EvoQuery q, str txt){
	Status s = (Status) `BROKEN`;
	q = set_status(s,q);
	return annotate(txt, q);
}

EvoQuery setStatusWarn(EvoQuery q){
	Status s = (Status) `WARNING`;
	return set_status(s, q);
}

EvoQuery setStatusWarn(EvoQuery q, str txt){
	Status s = (Status) `WARNING`;
	q = set_status(s,q);
	return annotate(txt, q);
}


EvoQuery setStatusChanged(EvoQuery q){
	Status s = (Status) `MODIFIED`;
	return set_status(s, q);
}

EvoQuery setStatusChanged(EvoQuery q, str txt){
	Status s = (Status) `MODIFIED`;
	q = set_status(s,q);
	return annotate(txt, q);
}

EvoQuery setStatusUnchanged(EvoQuery q){
	Status s = (Status) `UNCHANGED`;
	return set_status(s,q);
}

EvoQuery set_status(Status s, (EvoQuery)`<QlQuery q>`)
	= (EvoQuery) `<Status s> 
					'<QlQuery q>`;

EvoQuery set_status(Status s, (EvoQuery)`<Status old>  <QlQuery q>`){
	Status final = status_priority(s, old);
	return (EvoQuery) `<Status final> 
						'<QlQuery q>`;
}

EvoQuery set_status(Status s, (EvoQuery)`<Status old> <Annotation+ a> <QlQuery q>`){
	Status final = status_priority(s, old);
	return (EvoQuery) `<Status final> 
						'<Annotation+ a> 
						'<QlQuery q>`;
}

EvoQuery annotate(str text, EvoQuery q){
	Annotation a = parse(#Annotation, "#@ <text> @#");
	return set_annotation(a, q);
}

EvoQuery set_annotation(Annotation a, (EvoQuery)`<Status s>  <QlQuery q>`)
	= (EvoQuery) `<Status s>
				'<Annotation a> 
				'<QlQuery q>`;

EvoQuery set_annotation(Annotation a, (EvoQuery)`<Status s> <Annotation+ old> <QlQuery q>`)
	= (EvoQuery) `<Status s> 
				'<Annotation+ old> 
				'<Annotation a> 
				'<QlQuery q>`;


Status status_priority((Status) `MODIFIED`, s:(Status) `WARNING`) = s;
Status status_priority((Status) `MODIFIED`, s:(Status) `BROKEN`) = s;
Status status_priority((Status) `WARNING`, s:(Status) `BROKEN`) = s;
default Status status_priority(Status new, _) = new;
