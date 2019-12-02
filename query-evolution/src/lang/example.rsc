

module Bindings


import IO;





public Expression example = (Expression) `let a = c, c = d in a(c, d)`; 


// A: we literally change only a's to b's in this literal concrete example.

// Often when writing concrete syntax patterns it helps to start with a fully concrete example and test it,

// before introducing any pattern variables.

Expression changeAtoB(Expression e) 

  = visit(e) {

      case (Expression) `let a = c, c = d in a(c, d)` => (Expression) `let b = c, c = d in a(c, d)`

    };

   


// B: generalize by parametrizing the substitution by name, and also forget about the specific expression, and also forget about the other bindings. 

Expression changeFirstBinding1(Expression e, Name from, Name to) 

  = visit(e) {

      case (Expression) `let <Name from> = c, <{Binding ","}* rest> in <Expression exp>` => (Expression) `let <Name to> = c, c = d in <Expression exp>`

    };

    

// C: alternative to B, test for equality in a "when"

Expression changeFirstBinding2(Expression e, Name from, Name to) 

  = visit(e) {

      case (Expression) `let <Name n> = c, <{Binding ","}* rest> in <Expression exp>` => (Expression) `let <Name to> = c, c = d in <Expression exp>`

        when n := from // if n matches from it is the same name (== might fail due to different origin locations in the parse trees of the names)

    };

    

// C: alternative to C, test for equality in a "if"

Expression changeFirstBinding3(Expression e, Name from, Name to) 

  = visit(e) {

      case (Expression) `let <Name n> = c, <{Binding ","}* rest> in <Expression exp>` : {

         if (n := from) {

           insert  (Expression) `let <Name to> = c, <{Binding ","}* rest> in <Expression exp>`;

         } else {

           fail; // optionally backtrack to another case of the visit

         }

      }

    };    

    

// D: generalize B by ignoring the position of the substituted name in the list of bindings.

// I’d probably write something like this eventually:

Expression changeAnyBinding1(Expression e, Name from, Name to) 

  = visit(e) {

      case (Expression) `let <{Binding ","}* before>, <Name from> = <Expression f>, <{Binding ","}* rest> in <Expression exp>` 

        => (Expression) `let <{Binding ","}* before>, <Name to> = <Expression f>, <{Binding ","}* rest> in <Expression exp>`

    };       

    

// E: like D, but take str as arguments

Expression changeAnyBinding2(Expression e, str from, str to) 

  = visit(e) {

      case (Expression) `let <{Binding ","}* before>, <Name f> = <Expression g>, <{Binding ","}* rest> in <Expression exp>` 

        => (Expression) `let <{Binding ","}* before>, <Name t> = <Expression g>, <{Binding ","}* rest> in <Expression exp>`

      when f := [Name] from, t := [Name] to

    }; 

    

// E: like E, but flatten the name from tree instead of parsing the strings

Expression changeAnyBinding3(Expression e, str from, str to) 

  = visit(e) {

      case (Expression) `let <{Binding ","}* before>, <Name f> = <Expression g>, <{Binding ","}* rest> in <Expression exp>` 

        => (Expression) `let <{Binding ","}* before>, <Name t> = <Expression g>, <{Binding ","}* rest> in <Expression exp>`

      when f == from, Name t := [Name] to

    };               

