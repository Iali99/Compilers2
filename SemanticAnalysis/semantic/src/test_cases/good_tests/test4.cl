(* Type of initialization expression in let conform to the declared type of the identifier *)
class A {
    a : Int;
};

class B inherits A {
    b : Int;
};

class C inherits B {
    c : Int;
};

class D {
    d : Int;
};

class E {
    a : A <- new A;
    b : B <- new B;
    c : C <- new C;
    d : D <- new D;
    f1() : Int {
        let x : A <- c, y : B <- b, z : B <- c, w : D <- d in 
        {
            0;
        }
    };
};

class Main {
    main() : Int {
        0
    };
};
