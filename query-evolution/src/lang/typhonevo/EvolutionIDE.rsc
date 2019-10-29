module lang::typhonevo::EvolutionIDE

import ParseTree;
import Prelude;
import util::IDE; 
import util::ValueUI;
import IO;

import lang::typhonevo::EvoAbstractSyntax;
import lang::typhonevo::EvoCompiler;
import lang::typhonevo::BridgeML;

private str languageName = "TyphonQL";
private str extQL = "qevo";


public EvoSyntax parser(str x, loc l){
	return parse(#EvoSyntax, x, l);
}

private loc project(loc file) {
   assert file.scheme == "project";
   return |project:///|[authority = file.authority];
}


public void evalQuery(EvoSyntax x, loc selection) {
	evolved = test_modif(x);
	writeFile(project(x@\loc) + "/src/result.qevo", "<evolved>");
}

public void testString(EvoSyntax x, loc selection) {
	t = createMLOperators();	
	text(t);
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