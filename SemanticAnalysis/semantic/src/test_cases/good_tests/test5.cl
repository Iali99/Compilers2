(* Correct usage of unary operators *)
(* return types conforming with method body return*)

class B {
    a : Bool;
    b : Bool;
    c : Bool;
    d : Bool;
    f1() : Bool {
        {
            a;
        }
    };
    f2() : Bool {
        {
            not a;
            not b;
            not c;
            b;
        }
    };
};

class Main {
    main() : Int {
        0
    };
};
