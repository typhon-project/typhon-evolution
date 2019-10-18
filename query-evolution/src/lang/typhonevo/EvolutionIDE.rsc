module lang::typhonevo::EvolutionIDE

import ParseTree;
import Prelude;
import util::IDE; 
import util::ValueUI;

import lang::typhonevo::EvoAbstractSyntax;

private str languageName = "TyphonQL";
private str extQL = "qevo";


public EvoSyntax parser(str x, loc l){
	return parse(#EvoSyntax, x, l);
}


public void evalQuery(EvoSyntax x, loc selection) {
	m = implode(#EvoSyntax, x);
	println(m);
	text(m);
}

public void testString(EvoSyntax x, loc selection) {
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