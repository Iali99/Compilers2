(* Stack machine interpreter *)

class Main inherits IO {
	main() : Object {
	(*
		isloop 						-> used to end the loop when input is "x"
		mainStack 					-> the main stack  
		sTop,sTop1,sTop2,inp 		-> local variables
		a2iObj						-> A2I object  
	*)
	let isloop : Bool <- true, mainStack : StackOperation,
        sTop : String, sTop1 : String, sTop2 : String, inp : String, a2iObj : A2I <- new A2I

		-- take input and push to stack if input is not e,d 
		-- pop if input is e
		-- dOperation if input is d
	in{
		mainStack <- new StackOperation;
		while isloop loop { 		-- main program loop
		 	inp <- in_string();
		 	-- if inp is x then exit the loop
           		if inp = "x" then isloop <- false else 2=2 
           		fi;
		 	-- if inp is e then pop the top
		 	if inp = "e" then {
		 		-- pop from stack
		 		sTop <- mainStack.popOperation();
		 		-- if s is top then swap
		 		if sTop = "s" then {	-- swap top 2 elements
		 			sTop1 <- mainStack.popOperation();
		 			sTop2 <- mainStack.popOperation();
		 			mainStack.pushOperation(sTop1);
		 			mainStack.pushOperation(sTop2);
		 		}
		 		else {
		 			-- add nxt two elements
		 			if sTop = "+" then {
		 				-- add top 2 elements
		 				sTop1 <- mainStack.popOperation();
		 				sTop2 <- mainStack.popOperation();
		 				mainStack.pushOperation(a2iObj.i2a( a2iObj.a2i(sTop1) + a2iObj.a2i(sTop2) ));
		 			}
		 			else 
		 			-- push top if nothing else
		 				mainStack.pushOperation(sTop)
		 			fi;
		 		}
		 		fi;
		 	}
		 	else {
		 		-- if d then display stack
		 		if inp = "d" then mainStack.dOperation(mainStack.getTop()) else {
		 			mainStack.pushOperation(inp);
		 		}
		 		fi;
		 	}
		 	fi;
		}
		pool;
      }
	};
};

-- stack class
class Stack inherits IO {
	element : String;
	rest : Stack;

	-- initialise stack with top element as elem
	initStack (elem : String) : Object{
		element <- elem
	};
	-- make rest as rest1
	addRest (rest1 : Stack) : Object{
		rest <- rest1
	};
	-- return element
	getElem () : String {
		element
	};
	-- return rest
	getRest() : Stack {
		rest
	};
};

-- class with operators nad main stack
class StackOperation inherits IO {
	top : Stack <- new Stack;
	size : Int <- 0;
	temp : Stack ;
	temp2 : String;
    
    	getTop() : Stack{
      		top
    	};

	-- push to stack
	pushOperation (top1 : String) : Object {{
		size <- size + 1;
		temp <- new Stack;
		temp.initStack(top1);
		if size = 1 then top <- temp else {
			temp.addRest(top);
			top <- temp;
		} fi;  
    	}};
   	 -- pop from stack
	popOperation () : String {{
		size <- size - 1;
		temp2 <- top.getElem() ; 
		top <- top.getRest();
		temp2;
    	}};
    	-- display stack
	dOperation (stck : Stack) : Object {{
        if isvoid stck then 2=2 else{ 
			out_string(stck.getElem());
			out_string("\n");
			dOperation(stck.getRest());
        }
        fi;
    	}};
};

-- ++++++++++++++ A2I class copied from cool/examples +++++++++++++++++++ --
(*
   The class A2I provides integer-to-string and string-to-integer
conversion routines.  To use these routines, either inherit them
in the class where needed, have a dummy variable bound to
something of type A2I, or simpl write (new A2I).method(argument).
*)


(*
   c2i   Converts a 1-character string to an integer.  Aborts
         if the string is not "0" through "9"
*)
class A2I {

     c2i(char : String) : Int {
        if char = "0" then 0 else
        if char = "1" then 1 else
        if char = "2" then 2 else
        if char = "3" then 3 else
        if char = "4" then 4 else
        if char = "5" then 5 else
        if char = "6" then 6 else
        if char = "7" then 7 else
        if char = "8" then 8 else
        if char = "9" then 9 else
        { abort(); 0; }  (* the 0 is needed to satisfy the
                                  typchecker *)
        fi fi fi fi fi fi fi fi fi fi
     };

(*
   i2c is the inverse of c2i.
*)
     i2c(i : Int) : String {
        if i = 0 then "0" else
        if i = 1 then "1" else
        if i = 2 then "2" else
        if i = 3 then "3" else
        if i = 4 then "4" else
        if i = 5 then "5" else
        if i = 6 then "6" else
        if i = 7 then "7" else
        if i = 8 then "8" else
        if i = 9 then "9" else
        { abort(); ""; }  -- the "" is needed to satisfy the typchecker
        fi fi fi fi fi fi fi fi fi fi
     };

(*
   a2i converts an ASCII string into an integer.  The empty string
is converted to 0.  Signed and unsigned strings are handled.  The
method aborts if the string does not represent an integer.  Very
long strings of digits produce strange answers because of arithmetic 
overflow.
*)
     a2i(s : String) : Int {
        if s.length() = 0 then 0 else
        if s.substr(0,1) = "-" then ~a2i_aux(s.substr(1,s.length()-1)) else
        if s.substr(0,1) = "+" then a2i_aux(s.substr(1,s.length()-1)) else
           a2i_aux(s)
        fi fi fi
     };

(* a2i_aux converts the usigned portion of the string.  As a
   programming example, this method is written iteratively.  *)


     a2i_aux(s : String) : Int {
        (let int : Int <- 0 in        
           {        
               (let j : Int <- s.length() in
                  (let i : Int <- 0 in
                    while i < j loop
                        {
                            int <- int * 10 + c2i(s.substr(i,1));
                            i <- i + 1;
                        }
                    pool
                  )
               );
              int;
            }
        )
     };

(* i2a converts an integer to a string.  Positive and negative 
   numbers are handled correctly.  *)

    i2a(i : Int) : String {
        if i = 0 then "0" else 
        if 0 < i then i2a_aux(i) else
          "-".concat(i2a_aux(i * ~1)) 
        fi fi
    };
        
(* i2a_aux is an example using recursion.  *)                

    i2a_aux(i : Int) : String {
        if i = 0 then "" else 
            (let next : Int <- i / 10 in
                i2a_aux(next).concat(i2c(i - next * 10))
            )
        fi
    };

};
