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

// List of all the classes
class_list returns 		[ List<AST.class_> value ] 
					    @init {
					        $value  =  new ArrayList<AST.class>();
					    }
					    : 
					    (cl  =  class_ SEMICOLON { $value.add($cl.value); })+;

// A single class
class_ returns 			[ AST.class_ value ] 
    					: 
					    // class without inheritance
					    clss = CLASS type = TYPEID LBRACE fl = feature_list RBRACE {
					        // By default "Object" class is parent of all class
					        $value  =  new AST.class_($type.getText(), filename, "Object", 
					                $fl.value, $clss.getLine());
					    }
					    |
					    // class with inheritance
					    clss = CLASS type = TYPEID INHERITS parent = TYPEID LBRACE fl = feature_list RBRACE {
					        $value  =  new AST.class_($type.getText(), filename, $parent.getText(),
					                $fl.value, $clss.getLine());
					    };

// list of features
feature_list returns 	[ List<AST.feature> value ] 
					    @init {
					        $value  =  new ArrayList<AST.feature>();
					    }
					    : 
					    (fl  =  feature SEMICOLON { $value.add($fl.value); })*;

// a function or variable declaration (with or without assignment)
feature returns 		[ AST.feature value ]
						: 
					    // method without formal_list
					    fl1 = OBJECTID LPAREN RPAREN COLON type = TYPEID LBRACE b = expr RBRACE {
					        $value  =  new AST.method($fl1.getText(), new ArrayList<AST.formal>(), 
					            $type.getText(), $b.value, $fl1.getLine());
					    }
					    |
					    // method with formal_list
					    fl1 = OBJECTID LPAREN f = formal_list RPAREN COLON type = TYPEID LBRACE b = expr RBRACE {
					        $value  =  new AST.method($fl1.getText(), $f.value, $type.getText(), 
					            $b.value, $fl1.getLine());
					    }
					    // variable declaration without assignment
					    | 
					    fl2 = OBJECTID COLON t = TYPEID {
					        $value  =  new AST.attr($fl2.getText(), $t.getText(), new AST.no_expr($v.getLine()),
					            $fl2.getLine());
					    }
					    // variable declaration with assignment
					    | 
					    fl2 = OBJECTID COLON type = TYPEID ( ASSIGN ex = expr ) {
					        $value  =  new AST.attr($fl2.getText(), $type.getText(), $ex.value, $fl2.getLine());
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
					        $value  =  new AST.formal($forml.getText(), $type.getText(), $forml.getLine());
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
					        $value  =  new AST.branch($o.getText(), $t.getText(), $e.value, $o.getLine());
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
					    f = first_let_assgn { $value.add($f.value); }
					    ( la = nxt_let_assgn { $value.add($la.value); } )*;

// first varaible declaration in let statement (which doesnt start with comma)
first_let_assgn returns	[ AST.attr value ]
					    :
					    o = OBJECTID COLON t = TYPEID {
					        $value  =  new AST.attr($o.getText(), $t.getText(), new AST.no_expr($o.getLine()), $o.getLine());
					    }
					    |
					    o = OBJECTID COLON t = TYPEID ASSIGN e = expr {
					        $value  =  new AST.attr($o.getText(), $t.getText(), $e.value, $o.getLine());
					    };

// variable declarations in let statement which starts with comma (2nd and further declarations)
nxt_let_assgn returns 	[ AST.attr value ]
					    :
					    c = COMMA o = OBJECTID COLON t = TYPEID {
					        $value  =  new AST.attr($o.getText(), $t.getText(), new AST.no_expr($c.getLine()), $c.getLine());
					    }
					    |
					    c = COMMA o = OBJECTID COLON t = TYPEID ASSIGN e = expr{
					        $value  =  new AST.attr($o.getText(), $t.getText(), $e.value, $c.getLine());
					    };

/* All kinds of expressions */
expr returns 			[ AST.expression value ]
						: 
				        // dispatch (a function call of an object)
				        e1 = expr DOT oi = OBJECTID LPAREN el = expr_list RPAREN {
				            $value  =  new AST.dispatch($e1.value, $oi.getText(), $el.value, $e1.value.lineNo);
				        }
				        | 
				        // static dispatch
				        e1 = expr ATSYM t = TYPEID DOT oi = OBJECTID LPAREN el = expr_list RPAREN {
				            $value  =  new AST.static_dispatch($e1.value, $t.getText(), $oi.getText(), 
				                $el.value, $e1.value.lineNo);
				        }
				        | 
				        // function call of self
				        o = OBJECTID LPAREN el = expr_list RPAREN {
				            $value  =  new AST.dispatch(new AST.object("self", $o.getLine()), $o.getText(),
				                $el.value, $o.getLine());
				        }
				        | 
				        // if e2 then e2 else e3 fi
				        i = IF e1 = expr THEN e2 = expr ELSE e3 = expr FI {
				            $value  =  new AST.cond($e1.value, $e2.value, $e3.value, $i.getLine());
				        }
				        | 
				        // while e1 loop e2 pool
				        wh = WHILE e1 = expr LOOP e2 = expr POOL {
				            $value  =  new AST.loop($e1.value, $e2.value, $wh.getLine());
				        }
				        | 
				        // block expression
				        // { e1; e2; e3; ... }
				        lb = LBRACE bel = block_expr_list RBRACE {
				            $value  =  new AST.block($bel.value, $lb.getLine());
				        }
				        | 
				        // let statement with declarations and expression
				        l = LET lal = let_assgn_list IN e = expr {
				            AST.expression current_expr  =  $e.value;
				            int size  =  $lal.value.size();
				            for(int i = size-1; i> = 0; i--) {
				                AST.attr let_attr  =  $lal.value.get(i);
				                current_expr  =  new AST.let(let_attr.name, let_attr.typeid, let_attr.value, current_expr, $l.getLine());
				            }
				            $value  =  current_expr;
				        }
				        | 
				        // case e of bl esac
				        c = CASE e = expr OF bl = branch_list ESAC {
				            $value  =  new AST.typcase($e.value, $bl.value, $c.getLine());
				        }
				        | 
				        // new t
				        nw = NEW t = TYPEID {
				            $value  =  new AST.new_($t.getText(), $nw.getLine());
				        }
				        | 
				        // ~ e
				        tl = TILDE e = expr {
				            $value  =  new AST.comp($e.value, $tl.getLine());
				        }
				        | 
				        // isvoid expression
				        iv = ISVOID e1 = expr {
				            $value  =  new AST.isvoid($e1.value, $iv.getLine());
				        }
				        | 
				        // multiplication
				        e1 = expr STAR e2 = expr {
				            $value  =  new AST.mul($e1.value, $e2.value, $e1.value.lineNo);
				        }
				        | 
				        // division
				        e1 = expr SLASH e2 = expr {
				            $value  =  new AST.divide($e1.value, $e2.value, $e1.value.lineNo);
				        }
				        | 
				        // addition
				        e1 = expr PLUS e2 = expr {
				            $value  =  new AST.plus($e1.value, $e2.value, $e1.value.lineNo);
				        }
				        | 
				        // e1 - e2
				        e1 = expr MINUS e2 = expr {
				            $value  =  new AST.sub($e1.value, $e2.value, $e1.value.lineNo);
				        }
				        | 
				        // e1 < e2
				        e1 = expr LT e2 = expr {
				            $value  =  new AST.lt($e1.value, $e2.value, $e1.value.lineNo);
				        }
				        | 
				        // e1 < =  e2
				        e1 = expr LE e2 = expr {
				            $value  =  new AST.leq($e1.value, $e2.value, $e1.value.lineNo);
				        }
				        | 
				        // e1  =  e2
				        e1 = expr EQUALS e2 = expr {
				            $value  =  new AST.eq($e1.value, $e2.value, $e1.value.lineNo);
				        }
				        | 
				        // not expr
				        nt = NOT e1 = expr {
				            $value  =  new AST.neg($e1.value, $nt.getLine());
				        }
				        | 
				        // o <- e
				        <assoc = right>o = OBJECTID ASSIGN e = expr {
				            $value  =  new AST.assign($o.getText(), $e.value, $o.getLine());
				        }
				        | 
				        // (e)
				        LPAREN e = expr RPAREN {
				            $value  =  $e.value;
				        }
				        | 
				        // object
				        o = OBJECTID {
				            $value  =  new AST.object($o.getText(), $o.getLine());
				        }
				        | 
				        // integer constant
				        i = INT_CONST {
				            $value  =  new AST.int_const(Integer.parseInt($i.getText()), $i.getLine());
				        }
				        | 
				        // string constant
				        s = STR_CONST {
				            $value  =  new AST.string_const($s.getText(), $s.getLine());
				        }
				        | 
				        // bool constant
				        b = BOOL_CONST {
				            $value  =  new AST.bool_const("true".equalsIgnoreCase($b.getText()), $b.getLine());
				        };
// end of code