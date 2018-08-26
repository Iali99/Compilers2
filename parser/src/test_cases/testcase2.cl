(*
This program tests the following rules of the grammar.
RULES:
class_list: (class_ SEMICOLON)*;
class_ : CLASS TYPEID (INHERITS TYPEID)? LBRACE (feature SEMICOLON)* RBRACE;
feature : OBJECTID LPAREN ( formal (COMMA formal)* )? RPAREN COLON TYPEID LBRACE expr RBRACE
expr : OBJECTID LPAREN (expr (COMMA expr)* )? RPAREN
expr : STR_CONST
*)

(*
This program contains multiple classes.
The funtion foo does not have any arguments.
This program also contains a class with no features
*)
class A inherits IO {
  foo() : Bool {
    out_string("Hey there!\n");
  };
};

class B {

};
