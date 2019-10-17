module lang::typhonevo::EvolutionIDE

import ParseTree;
import Prelude;
import util::IDE; 
import util::ValueUI;

import lang::typhonevo::EvoAbstractSyntax;

private str languageName = "TyphonQL";
private str extQL = "qevo";


public EvolutionScript parser(str x, loc l){
	return parse(#EvolutionScript, x, l);
}


public void evalQuery(EvolutionScript x, loc selection) {
	text("bleh");
}

public void testString(EvolutionScript x, loc selection) {
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