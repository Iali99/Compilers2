(* Illustrates bool expressions *)

class A {
    f1(x : Bool, y : String) : Int {
        {
            if x then 0 else 1 fi;
            0;
        }
    };
};

class B {
    f2(x : Bool, y : Bool, z : Int) : Int {
        {
            while x loop z <- z+1 pool;
            while y loop z <- z+1 pool;
            0;
        }
    };
};

class Main {
    main() : Int {
        0
    };
};