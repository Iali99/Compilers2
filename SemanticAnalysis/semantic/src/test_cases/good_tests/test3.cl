(* Predicate of a conditional or a loop is Bool *)

class A {
    f1(x : Bool, y : Bool) : Int {
        {
            if x then 0 else 1 fi;
            if y then 0 else 1 fi;
            0;
        }
    };
};

class B {
    f2(x : Bool, y : Bool, z : Int) : Int {
        {
            while x loop z <- z+1 pool;
            0;
        }
    };
};

class Main {
    main() : Int {
        0
    };
};
