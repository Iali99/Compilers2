(* Incorrect usage of unary operators *)
(* return types not conforming with method body return*)

class A {
    a : Int;
};

class B {
    a : A;
    b : Int;
    c : String;
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
            "ABC";
        }
    };
};

class Main {
    main() : Int {
        0
    };
};
