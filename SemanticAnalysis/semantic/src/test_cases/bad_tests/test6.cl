(* attribute with non-conforming or undefined type *)

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
    a2 : B <- new A;
    a3 : B <- new C;
    a4 : B <- new D;
};

class Main {
    main() : Int {
        0
    };
};