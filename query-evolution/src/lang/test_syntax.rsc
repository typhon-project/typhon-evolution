module lang::test_syntax
import IO;

  
lexical Number = [0-9]+;  
lexical Name = [a-z]+;


syntax Expression 
  = Name name
  | Number number

  