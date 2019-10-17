module lang::typhonevo::EvoAbstractSyntax

extend lang::typhonql::Query;

syntax EvoSyntax 
	= evosyntax: {Query ";"}* queries;
