parser grammar CoolParser;

options {
    tokenVocab  =  CoolLexer;
}

@header{
    import java.util.List;
}

@members{
    String filename;
    public void setFilename(String f){
        filename  =  f;
    }

/*
    DO NOT EDIT THE FILE ABOVE THIS LINE
    Add member functions, variables below.
*/
	int cur_ln;
}

/*
    Add appropriate actions to grammar rules for building AST below.
*/

// Root of all grammars. Entire program converges to this.
program returns 		[ AST.program value ] 
						: 
					    cl = class_list EOF {
					        $value  =  new AST.program($cl.value, $cl.value.get(0).lineNo);    
					    };                    

// call lists 
// (class_ SEMICOLON)*;
class_list returns 		[ List<AST.class_> value ] 
					    @init {
					        $value  =  new ArrayList<AST.class>();
					    }
					    : 
					    (cl  =  class_ SEMICOLON { $value.add($cl.value); })+;

// A class
// CLASS TYPEID (INHERITS TYPEID)? LBRACE (feature SEMICOLON)* RBRACE;
class_ returns 			[ AST.class_ value ] 
    					: 
					    // class with no inheritance
					    clss = CLASS type = TYPEID LBRACE fl = feature_list RBRACE {
					    	cur_ln = $clss.getLine();
					        // taking "Object" as the parent class
					        $value  =  new AST.class_($type.getText(), filename, "Object", 
					                $fl.value, cur_ln);
					    }
					    |
					    // class with inheritance
					    clss = CLASS type = TYPEID INHERITS parent = TYPEID LBRACE fl = feature_list RBRACE {
					    	cur_ln = $clss.getLine();
					        $value  =  new AST.class_($type.getText(), filename, $parent.getText(),
					                $fl.value, cur_ln);
					    };

// list of features used above
// (feature SEMICOLON)*
feature_list returns 	[ List<AST.feature> value ] 
					    @init {
					        $value  =  new ArrayList<AST.feature>();
					    }
					    : 
					    (fl  =  feature SEMICOLON { $value.add($fl.value); })*;

// a feature
// OBJECTID LPAREN ( formal (COMMA formal)*)? RPAREN COLON TYPEID LBRACE expr RBRACE 
// | OBJECTID COLON TYPEID ( ASSIGN expr )?
feature returns 		[ AST.feature value ]
						: 
					    // method with (formal_list)?
					    fl1 = OBJECTID LPAREN f = formal_list RPAREN COLON type = TYPEID LBRACE b = expr RBRACE {
					    	cur_ln = $fl1.getLine();
					        $value  =  new AST.method($fl1.getText(), $f.value, $type.getText(), 
					            $b.value, cur_ln);
					    }
					    // variable declaration without assignment expression
					    | 
					    fl2 = OBJECTID COLON t = TYPEID {
					    	cur_ln = $fl2.getLine();
					        $value  =  new AST.attr($fl2.getText(), $t.getText(), new AST.no_expr($v.getLine()),
					            cur_ln);
					    }
					    // variable declaration with assignment expression
					    | 
					    fl2 = OBJECTID COLON type = TYPEID ( ASSIGN ex = expr ) {
					    	cur_ln = $fl2.getLine();
					        $value  =  new AST.attr($fl2.getText(), $type.getText(), $ex.value, cur_ln);
					    };

// list of formals used above
// ( formal (COMMA formal)*)?
formal_list returns 	[ List<AST.formal> value ]
					    @init {
					        $value  =  new ArrayList<AST.formal>();
					    }
					    : 
					    ( 	f1 = formal { $value.add($f1.value); } 
					    	(COMMA f2 = formal { $value.add($f2.value); })*
					    )?;

// a formal (variable declaration)
formal returns 			[ AST.formal value ] 
						: 
					    forml = OBJECTID COLON type = TYPEID {
					    	cur_ln = $forml.getLine();
					        $value  =  new AST.formal($forml.getText(), $type.getText(), cur_ln);
					    };

// a branche list in type case
// (OBJECTID COLON TYPEID DARROW expr SEMICOLON)+
branch_list returns 	[ List<AST.branch> value ]
					    @init {
					        $value  =  new ArrayList<AST.branch>();
					    }
					    : 
					    (b = branch { $value.add($b.value); })+;

// a branch in type case
// (OBJECTID COLON TYPEID DARROW expr SEMICOLON)
branch returns 			[ AST.branch value ] 
						:
					    o = OBJECTID COLON t = TYPEID DARROW e = expr SEMICOLON {
					    	cur_ln = $o.getLine();
					        $value  =  new AST.branch($o.getText(), $t.getText(), $e.value, cur_ln);
					    };

// list of expressions with semicolon
block_expr_list returns [ List<AST.expression> value ] 
					    @init {
					        $value  =  new ArrayList<AST.expression>();
					    }
					    : 
					    (e = expr SEMICOLON { $value.add($e.value); } )+;

// list of expressions separated by comma
expr_list returns 		[ List<AST.expression> value ]
					    @init {
					        $value  =  new ArrayList<AST.expression>();
					    }
					    : 
					    (
					        e1 = expr { $value.add($e1.value); }
					       	(COMMA e2 = expr { $value.add($e2.value); })*
					    )?;

/* LET assignments */
// let assignment list
// OBJECTID COLON TYPEID ( ASSIGN expr )? ( COMMA OBJECTID COLON TYPEID ( ASSIGN expr )?)*
let_assgn_list returns 	[ List<AST.attr> value ]
					    @init {
					        $value  =  new ArrayList<AST.attr>();
					    }
					    : 
					    // first let assignment
					    frst = let_assgn { $value.add($frst.value); }
					    // next list of let assignments starting with a comma
					    ( COMMA nxt_assgn = let_assgn { $value.add($nxt_assgn.value); } )*;

// a let assignment
// OBJECTID COLON TYPEID ( ASSIGN expr )?
let_assgn returns	[ AST.attr value ]
					    :
					    o = OBJECTID COLON t = TYPEID {
					    	cur_ln = $o.getLine();
					        $value  =  new AST.attr($o.getText(), $t.getText(), new AST.no_expr($o.getLine()), cur_ln);
					    }
					    |
					    o = OBJECTID COLON t = TYPEID ASSIGN e = expr {
					    	cur_ln = $o.getLine();
					        $value  =  new AST.attr($o.getText(), $t.getText(), $e.value, cur_ln);
					    };

/* expressions */
expr returns 			[ AST.expression value ]
						: 
						// expr (ATSYM TYPEID)? DOT OBJECTID LPAREN (expr (COMMA expr)*)? RPAREN
				        // dispatch
				        e1 = expr DOT oi = OBJECTID LPAREN el = expr_list RPAREN {
				        	cur_ln = $e1.value.lineNo;
				            $value  =  new AST.dispatch($e1.value, $oi.getText(), $el.value, cur_ln);
				        }
				        | 
				        // static dispatch
				        e1 = expr ATSYM t = TYPEID DOT oi = OBJECTID LPAREN el = expr_list RPAREN {
				        	cur_ln = $e1.value.lineNo;
				            $value  =  new AST.static_dispatch($e1.value, $t.getText(), $oi.getText(), 
				                $el.value, cur_ln);
				        }
				        | 
				        // OBJECTID LPAREN (expr (COMMA expr)*)? RPAREN
				        o = OBJECTID LPAREN el = expr_list RPAREN {
				        	cur_ln = $o.getLine();
				            $value  =  new AST.dispatch(new AST.object("self", $o.getLine()), $o.getText(),
				                $el.value, cur_ln);
				        }
				        | 
				        // IF expr THEN expr ELSE expr FI
				        i = IF e1 = expr THEN e2 = expr ELSE e3 = expr FI {
				        	cur_ln = $i.getLine();
				            $value  =  new AST.cond($e1.value, $e2.value, $e3.value, cur_ln);
				        }
				        | 
				        // WHILE expr LOOP expr POOL
				        whl = WHILE e1 = expr LOOP e2 = expr POOL {
				        	cur_ln = $whl.getLine();
				            $value  =  new AST.loop($e1.value, $e2.value, cur_ln);
				        }
				        | 
				        // LBRACE (expr SEMICOLON)* RBRACE
				        // { e1; e2; e3; ... }
				        lb = LBRACE bel = block_expr_list RBRACE {
				        	cur_ln = $lb.getLine();
				            $value  =  new AST.block($bel.value, cur_ln);
				        }
				        | 
				        // LET OBJECTID COLON TYPEID ( ASSIGN expr )? ( COMMA OBJECTID COLON TYPEID ( ASSIGN expr )?)* IN expr
				        lt = LET lal = let_assgn_list IN e = expr {
				            int size  =  $lal.value.size() - 1;
				            AST.expression this_expr  =  $e.value;
				            while(size>=0) {
				                AST.attr let_attr  =  $lal.value.get(size);
				                cur_ln = $lt.getLine();
				                this_expr  =  new AST.let(let_attr.name, let_attr.typeid, let_attr.value, this_expr, cur_ln);
				            }
				            $value  =  this_expr;
				            size = size - 1;
				        }
				        | 
				        // CASE expr OF (OBJECTID COLON TYPEID DARROW expr SEMICOLON)+ ESAC
				        c = CASE ex = expr OF bl = branch_list ESAC {
				        	cur_ln = $c.getLine();
				            $value  =  new AST.typcase($ex.value, $bl.value, cur_ln);
				        }
				        | 
				        // NEW TYPEID
				        nw = NEW t = TYPEID {
				        	cur_ln = $nw.getLine();
				            $value  =  new AST.new_($t.getText(), cur_ln);
				        }
				        | 
				        // TILDE expr
				        tlde = TILDE e = expr {
				        	cur_ln = $tlde.getLine();
				            $value  =  new AST.comp($e.value, cur_ln);
				        }
				        | 
				        // ISVOID expr
				        isv = ISVOID e1 = expr {
				        	cur_ln = $isv.getLine();
				            $value  =  new AST.isvoid($e1.value, cur_ln);
				        }
				        | 
				        // expr STAR expr
				        emul = expr STAR exp = expr {
				            $value  =  new AST.mul($emul.value, $exp.value, $emul.value.lineNo);
				        }
				        | 
				        // expr SLASH expr
				        ediv = expr SLASH exp = expr {
				            $value  =  new AST.divide($ediv.value, $exp.value, $ediv.value.lineNo);
				        }
				        | 
				        // expr PLUS expr
				        eadd = expr PLUS exp = expr {
				            $value  =  new AST.plus($eadd.value, $exp.value, $eadd.value.lineNo);
				        }
				        | 
				        // expr MINUS expr
				        esub = expr MINUS exp = expr {
				            $value  =  new AST.sub($esub.value, $exp.value, $esub.value.lineNo);
				        }
				        | 
				        // expr LT expr
				        elt = expr LT exp = expr {
				            $value  =  new AST.lt($elt.value, $exp.value, $elt.value.lineNo);
				        }
				        | 
				        // expr LE expr
				        ele = expr LE exp = expr {
				            $value  =  new AST.leq($ele.value, $exp.value, $ele.value.lineNo);
				        }
				        | 
				        // expr EQUALS expr
				        ee = expr EQUALS exp = expr {
				            $value  =  new AST.eq($ee.value, $exp.value, $ee.value.lineNo);
				        }
				        | 
				        // NOT expr
				        ent = NOT exp = expr {
				            $value  =  new AST.neg($exp.value, $ent.getLine());
				        }
				        | 
				        // <assoc=right>OBJECTID ASSIGN expr
				        <assoc = right> obj_id = OBJECTID ASSIGN e = expr {
				            $value  =  new AST.assign($obj_id.getText(), $e.value, $obj_id.getLine());
				        }
				        | 
				        // LPAREN expr RPAREN
				        LPAREN exp = expr RPAREN {
				            $value  =  $exp.value;
				        }
				        | 
				        // OBJECTID
				        ob = OBJECTID {
				            $value  =  new AST.object($ob.getText(), $ob.getLine());
				        }
				        | 
				        // INT_CONST
				        iConst = INT_CONST {
				            $value  =  new AST.int_const(Integer.parseInt($iConst.getText()), $iConst.getLine());
				        }
				        | 
				        // STR_CONST   
				        sConst = STR_CONST {
				            $value  =  new AST.string_const($sConst.getText(), $sConst.getLine());
				        }
				        | 
				        // BOOL_CONST  
				        bConst = BOOL_CONST {
				            $value  =  new AST.bool_const("true".equalsIgnoreCase($bConst.getText()), $bConst.getLine());
				        };
// end of code