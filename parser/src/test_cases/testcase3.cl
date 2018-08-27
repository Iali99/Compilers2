(*
This program tests the following rules of the grammar.
RULES:
feature : OBJECTID COLON TYPEID (ASSIGN  expr)?
expr :  WHILE expr LOOP expr POOL
expr :  CASE expr OF (OBJECTID COLON TYPEID DARROW expr SEMICOLON)+ ESAC
*)

class Test {
  x : Int <- 1;
  foo() : Int {
    {
      while(x < 10) loop
      {
        x <- x+1;
      }
      pool;
      case x of
        x : Int => 1;
      esac;
      }
  };
};
