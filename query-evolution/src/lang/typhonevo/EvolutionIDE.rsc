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
import lang::typhonml::XMIReader;
import lang::typhonml::Util;

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
	loc l = project(selection);
	evolved = evolve(x, l);
	writeFile(project(x@\loc) + "/src/result.qevo", "<evolved>");
}

public void testString(EvoSyntax x, loc selection) {
	results = [f.path | /file(f) <- crawl(project(x@\loc)), f.extension == "xmi"];
	first = results[0];
	chosen = prompt("For which file do you want to upgrade <results>");
	text(first);
}

public void getIDEid(Tree t, loc selection){
	str xmi = readFile(|project://query-evolution/src/complexModelWithChangeOperators.xmi|);
  	Schema s = loadSchemaFromXMI(xmi);
	text(s);
}

public set[Contribution] languageContrib = {
	popup(
		menu("TyphonEvolution",[
		    action("Evolve", evalQuery),
		    action("getIDE", getIDEid)
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
