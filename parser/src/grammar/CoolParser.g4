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
					    // method without formal_list
					    fl1 = OBJECTID LPAREN RPAREN COLON type = TYPEID LBRACE b = expr RBRACE {
					    	cur_ln = $fl1.getLine();
					        $value  =  new AST.method($fl1.getText(), new ArrayList<AST.formal>(), 
					            $type.getText(), $b.value, cur_ln);
					    }
					    |
					    // method with formal_list
					    fl1 = OBJECTID LPAREN f = formal_list RPAREN COLON type = TYPEID LBRACE b = expr RBRACE {
					    	cur_ln = $fl1.getLine();
					        $value  =  new AST.method($fl1.getText(), $f.value, $type.getText(), 
					            $b.value, cur_ln);
					    }
					    // variable declaration without assignment
					    | 
					    fl2 = OBJECTID COLON t = TYPEID {
					    	cur_ln = $fl2.getLine();
					        $value  =  new AST.attr($fl2.getText(), $t.getText(), new AST.no_expr($v.getLine()),
					            cur_ln);
					    }
					    // variable declaration with assignment
					    | 
					    fl2 = OBJECTID COLON type = TYPEID ( ASSIGN ex = expr ) {
					    	cur_ln = $fl2.getLine();
					        $value  =  new AST.attr($fl2.getText(), $type.getText(), $ex.value, cur_ln);
					    };
					    
// list of formals
formal_list returns 	[ List<AST.formal> value ]
					    @init {
					        $value  =  new ArrayList<AST.formal>();
					    }
					    : 
					    f1 = formal { $value.add($f1.value); } 
					    (COMMA f2 = formal { $value.add($f2.value); })*;

// variable declarations
formal returns 			[ AST.formal value ] 
						: 
					    forml = OBJECTID COLON type = TYPEID {
					    	cur_ln = $forml.getLine();
					        $value  =  new AST.formal($forml.getText(), $type.getText(), cur_ln);
					    };

// list branches of type case
branch_list returns 	[ List<AST.branch> value ]
					    @init {
					        $value  =  new ArrayList<AST.branch>();
					    }
					    : 
					    (b = branch { $value.add($b.value); })+;

// a branch in type case
branch returns 			[ AST.branch value ] 
						:
					    o = OBJECTID COLON t = TYPEID DARROW e = expr SEMICOLON {
					    	cur_ln = $o.getLine();
					        $value  =  new AST.branch($o.getText(), $t.getText(), $e.value, cur_ln);
					    };

// list of expressions ending with semicolon
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
// list of assignments in a let statement
let_assgn_list returns 	[ List<AST.attr> value ]
					    @init {
					        $value  =  new ArrayList<AST.attr>();
					    }
					    : 
					    frst = let_assgn { $value.add($frst.value); }
					    ( COMMA nxt_assgn = let_assgn { $value.add($nxt_assgn.value); } )*;

// first varaible declaration in let statement (which doesnt start with comma)
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

/* All kinds of expressions */
expr returns 			[ AST.expression value ]
						: 
				        // dispatch (a function call of an object)
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
				        // function call of self
				        o = OBJECTID LPAREN el = expr_list RPAREN {
				        	cur_ln = $o.getLine();
				            $value  =  new AST.dispatch(new AST.object("self", $o.getLine()), $o.getText(),
				                $el.value, cur_ln);
				        }
				        | 
				        // if e2 then e2 else e3 fi
				        i = IF e1 = expr THEN e2 = expr ELSE e3 = expr FI {
				        	cur_ln = $i.getLine();
				            $value  =  new AST.cond($e1.value, $e2.value, $e3.value, cur_ln);
				        }
				        | 
				        // while e1 loop e2 pool
				        whl = WHILE e1 = expr LOOP e2 = expr POOL {
				        	cur_ln = $whl.getLine();
				            $value  =  new AST.loop($e1.value, $e2.value, cur_ln);
				        }
				        | 
				        // block expression
				        // { e1; e2; e3; ... }
				        lb = LBRACE bel = block_expr_list RBRACE {
				        	cur_ln = $lb.getLine();
				            $value  =  new AST.block($bel.value, cur_ln);
				        }
				        | 
				        // let statement with declarations and expression
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
				        // case e of bl esac
				        c = CASE ex = expr OF bl = branch_list ESAC {
				        	cur_ln = $c.getLine();
				            $value  =  new AST.typcase($ex.value, $bl.value, cur_ln);
				        }
				        | 
				        // new t
				        nw = NEW t = TYPEID {
				        	cur_ln = $nw.getLine();
				            $value  =  new AST.new_($t.getText(), cur_ln);
				        }
				        | 
				        // ~ e
				        tlde = TILDE e = expr {
				        	cur_ln = $tlde.getLine();
				            $value  =  new AST.comp($e.value, cur_ln);
				        }
				        | 
				        // isvoid expression
				        isv = ISVOID e1 = expr {
				        	cur_ln = $isv.getLine();
				            $value  =  new AST.isvoid($e1.value, cur_ln);
				        }
				        | 
				        // multiplication
				        emul = expr STAR exp = expr {
				            $value  =  new AST.mul($emul.value, $exp.value, $emul.value.lineNo);
				        }
				        | 
				        // division
				        ediv = expr SLASH exp = expr {
				            $value  =  new AST.divide($ediv.value, $exp.value, $ediv.value.lineNo);
				        }
				        | 
				        // addition
				        eadd = expr PLUS exp = expr {
				            $value  =  new AST.plus($eadd.value, $exp.value, $eadd.value.lineNo);
				        }
				        | 
				        // e1 - e2
				        esub = expr MINUS exp = expr {
				            $value  =  new AST.sub($esub.value, $exp.value, $esub.value.lineNo);
				        }
				        | 
				        // e1 < e2
				        elt = expr LT exp = expr {
				            $value  =  new AST.lt($elt.value, $exp.value, $elt.value.lineNo);
				        }
				        | 
				        // e1 < =  e2
				        ele = expr LE exp = expr {
				            $value  =  new AST.leq($ele.value, $exp.value, $ele.value.lineNo);
				        }
				        | 
				        // e1  =  e2
				        ee = expr EQUALS exp = expr {
				            $value  =  new AST.eq($ee.value, $exp.value, $ee.value.lineNo);
				        }
				        | 
				        // not expr
				        ent = NOT exp = expr {
				            $value  =  new AST.neg($exp.value, $ent.getLine());
				        }
				        | 
				        // o <- e
				        <assoc = right> obj_id = OBJECTID ASSIGN e = expr {
				            $value  =  new AST.assign($obj_id.getText(), $e.value, $obj_id.getLine());
				        }
				        | 
				        // (e)
				        LPAREN exp = expr RPAREN {
				            $value  =  $exp.value;
				        }
				        | 
				        // object
				        ob = OBJECTID {
				            $value  =  new AST.object($ob.getText(), $ob.getLine());
				        }
				        | 
				        // integer constant
				        iConst = INT_CONST {
				            $value  =  new AST.int_const(Integer.parseInt($iConst.getText()), $iConst.getLine());
				        }
				        | 
				        // string constant
				        sConst = STR_CONST {
				            $value  =  new AST.string_const($sConst.getText(), $sConst.getLine());
				        }
				        | 
				        // bool constant
				        bConst = BOOL_CONST {
				            $value  =  new AST.bool_const("true".equalsIgnoreCase($bConst.getText()), $bConst.getLine());
				        };
// end of code