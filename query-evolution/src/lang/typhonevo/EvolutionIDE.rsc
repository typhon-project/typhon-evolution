module lang::typhonevo::EvolutionIDE

import ParseTree;
import Prelude;
import util::IDE; 
import util::ValueUI;

import lang::typhonql::Query;

private str languageName = "TyphonQL";
private str extQL = "qevo";


public Query parser(str x, loc l){
	return parse(#Query, x, l);
}


public void evalQuery(Query x, loc selection) {
	text("bleh");
}

public void testString(Query x, loc selection) {
	text("Test display string");
}

public set[Contribution] languageContrib = {
	popup(
		menu("TyphonEvolution",[
		    action("Evolve", evalQuery),
		    action("Test", testString)
	    ])
  	)
};


void setEvoIDE(){

	registerLanguage(languageName, extQL, parser);
	registerContributions(languageName, languageContrib);

}