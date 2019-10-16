module lang::pico::IDE

import Prelude;
import util::IDE;
import util::ValueUI;

import vis::Figure;
import vis::Render;

import lang::pico::Abstract;
import lang::pico::Syntax;
import lang::pico::Load;


private str PicoName = "Pico";
private str picoExt = "pic";


Tree parser(str x, loc l) {
    return parse(#Program, x, l);
}

public void evalPicoProgram(Program x, loc selection) {
	m = implode(#PROGRAM, x);
	println(m);
	text(m);
}

public list[str] readme(){
	return ["Test de readme"];
}

public void showReadMe(Program x, loc selection){
	println(readme());
	text(readme());
}

public set[Contribution] Pico_CONTRIBS = {
	popup(
		menu("Pico",[
		    action("show eval", evalPicoProgram),
		    action("show Readme", showReadMe),
		    action("Example item", void (Program t, loc s) { text("<t> @ <s>");})
	    ])
  	)
};

public void setUpIDE(){
	registerLanguage(PicoName, picoExt, parser);
	registerContributions(PicoName, Pico_CONTRIBS);
}
