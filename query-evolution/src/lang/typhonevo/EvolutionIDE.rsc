module lang::typhonevo::EvolutionIDE

import ParseTree;
import Prelude;
import util::IDE; 
import util::ValueUI;
import IO;
import util::FileSystem;
import util::Prompt;

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
	results = [f.path | /file(f) <- crawl(project(x@\loc)), f.extension == "xmi"];
	first = results[0];
	chosen = prompt("For which file do you want to upgrade <results>");
	text(first);
}

public void getIDEid(Tree t, loc selection){
	text(getIDEID());
}

public set[Contribution] languageContrib = {
	popup(
		menu("TyphonEvolution",[
		    action("Evolve", evalQuery),
		    action("Test", testString),
		    action("Find ID", getIDEid)
	    ])
  	)
};

public void generateEvolutionScript(Tree t, loc l){
	text("youpie");
}

public set[Contribution] xmiContrib = {
	popup(
		menu("TyphonEvolution",[
		    action("Generate Script", generateEvolutionScript)
	    ])
  	)
};


void setEvoIDE(){

	registerLanguage(languageName, extQL, parser);
	registerContributions(languageName, languageContrib);
	
	str xmi_editor = "org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart";
	registerNonRascalContributions(xmi_editor, xmiContrib);
}
