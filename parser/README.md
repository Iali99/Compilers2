# Parser (phase B)
> Sudhanshu Chawhan - cs16btech11037, Irfan Ali - cs16btech11019
## Introduction
A parser for COOL. 
The assignment makes use of the parser
generator (called ANTLR). 
The output of the parser is an abstract syntax tree (AST). 
This AST is generated using semantic actions of the parser generator.

## Parser Design

* The grammer rules for Cool were provided in the Cool Manual.
* For each of the grammer rule, a set of actions have been added in the ANTLR grammer file.
* The grammer rules are read and processed token by token. ANTLR provides an easy to use API for the purpose. 
* The classes and functions defined in AST.java are used in these actions. Each rule returns an AST node and it's value.
* For a syntactically correct program, the code outputs the corresponding AST.
* For a syntactically incorrect program, the code outputs the error messages.
* The parser works by taking as input the token stream of a COOL program, for every correct grammer rule formed by the token, the corresponding node is added to the AST.
