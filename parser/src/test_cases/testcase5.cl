(*
This program has errors.
The variable x has no type.
The function foo has no return type.
The if construct doesnot end with fi
*)

(*
Error message:
*)

class Test {
  x <- 10;
  foo() {
    if (x < 10) then
      1
    else
      2
  };
};
