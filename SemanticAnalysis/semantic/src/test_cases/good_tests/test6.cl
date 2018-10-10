(* attribute with conforming type *)

class A {
    a : Int;
};

class B inherits A {
    b : Int;
};

class C inherits B {
    c : Int;
};

class E {
    a1 : B <- new B;
    a3 : B <- new C;
};

class Main {
    main() : Int {
        0
    };
};