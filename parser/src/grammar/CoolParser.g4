parser grammar CoolParser;

options {
	tokenVocab = CoolLexer;
}

@header{
	import java.util.List;
}

@members{
	String filename;
	public void setFilename(String f){
		filename = f;
	}

/*
	DO NOT EDIT THE FILE ABOVE THIS LINE
	Add member functions, variables below.
*/

}

/*
	Add appropriate actions to grammar rules for building AST below.
*/

program returns [AST.program value]	: 
			cl=class_list EOF 
				{
					$value = new AST.program($cl.value, $cl.value.get(0).lineNo);
				}
		;					

class_list: (class_ SEMICOLON)*;

class_ : CLASS TYPEID (INHERITS TYPEID)? LBRACE (feature SEMICOLON)* RBRACE;

feature : OBJECTID LPAREN ( formal (COMMA formal)*)? RPAREN COLON TYPEID LBRACE expr RBRACE 
		| OBJECTID COLON TYPEID ( ASSIGN expr )?
		;

formal	: OBJECTID COLON TYPEID ;

expr	: expr (ATSYM TYPEID)? DOT OBJECTID LPAREN (expr (COMMA expr)*)? RPAREN
		| OBJECTID LPAREN (expr (COMMA expr)*)? RPAREN
		| IF expr THEN expr ELSE expr FI
		| WHILE expr LOOP expr POOL
		| LBRACE (expr SEMICOLON)* RBRACE
		| LET OBJECTID COLON TYPEID ( ASSIGN expr )? ( COMMA OBJECTID COLON TYPEID ( ASSIGN expr )?)* IN expr
		| CASE expr OF (OBJECTID COLON TYPEID DARROW expr SEMICOLON)+ ESAC
		| NEW TYPEID
		| TILDE expr
		| ISVOID expr
		| expr STAR expr
		| expr SLASH expr
		| expr PLUS expr
		| expr MINUS expr
		| expr LT expr
		| expr LE expr
		| expr EQUALS expr
		| NOT expr
		| <assoc=right>OBJECTID ASSIGN expr
		| LPAREN expr RPAREN
		| OBJECTID
		| INT_CONST
		| STR_CONST
		| BOOL_CONST
		;
