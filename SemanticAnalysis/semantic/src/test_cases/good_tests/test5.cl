(* Correct usage of unary operators *)
(* return types conforming with method body return*)

class B {
    a : Bool;
    b : Bool;
    c : Bool;
    d : Bool;
    f1() : Int {
        {
            ~a;
            ~c;
            ~d;
            0;
        }
    };
    f2() : Int {
        {
            not a;
            not b;
            not c;
            0;
        }
    };
};

class Main {
    main() : Int {
        0
    };
};
