(* Illustrates method redefinition *)

class A {
    f(x : Int, x : Int) : Int {
        0
    };
};

class B {
    f(x : Int, x : Int) : Int {
        1
    };
};

class Main {
    main() : Int {
        0
    };
};
