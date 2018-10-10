(* Type of initialization expression in let does not conform to the declared type of the identifier *)
(* attribute redefined in the subclass*)
class A {
    a : Int;
};

class B inherits A {
	a : Int;
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
        let x : B <- a, y : B <- b, z : B <- c, w : B <- d in 
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