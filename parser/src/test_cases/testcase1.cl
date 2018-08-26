(*
This program tests the following rules of the grammar.
RUlES :
class_list: (class_ SEMICOLON)*;
class_ : CLASS TYPEID (INHERITS TYPEID)? LBRACE (feature SEMICOLON)* RBRACE;
feature : OBJECTID LPAREN ( formal (COMMA formal)* )? RPAREN COLON TYPEID LBRACE expr RBRACE
feature : OBJECTID COLON TYPEID ( ASSIGN expr )?
formal	: OBJECTID COLON TYPEID ;
expr : IF expr THEN expr ELSE expr FI
expr : LBRACE (expr SEMICOLON)* RBRACE
expr : LET OBJECTID COLON TYPEID ( ASSIGN expr )? ( COMMA OBJECTID COLON TYPEID ( ASSIGN expr )?)* IN expr
expr : expr STAR expr
expr : expr PLUS expr
expr : expr MINUS expr
expr : expr LE expr
expr : INT_CONST
expr : BOOL_CONST
expr : OBJECTID
*)

class Test inherits TestParent {
  x : Int;
  foo(param : Int) : Bool {
    let y : Int <- x*2+1-43
    in
    {
      if (y <= 0) then
        true
      else
        false
      fi;
    }
  };
};
