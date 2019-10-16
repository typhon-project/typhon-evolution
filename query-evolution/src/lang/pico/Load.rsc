module lang::pico::Load

import Prelude;
import lang::pico::Abstract;
import lang::pico::Syntax;

public PROGRAM  load(str txt) = implode(#PROGRAM, parse(#Program, txt));