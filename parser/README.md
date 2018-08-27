# Parser (phase B)
>Irfan Ali - cs16btech11019 , Sudhanshu Chawhan - cs16btech11037
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

## Test Cases

* There are various test cases in the folder test_cases.
* There are 3 test cases which check the parser rules and are without errors. There are 2 other test cases which have parsing errors in them.
* The test cases from testcase1.cl to testcase3.cl check the rules. The rules of the grammar which they check are given in the respective files as comments.
* testcase4.cl and testcase5.cl have errors in them. The errors are described in the respective files as comments.
* There is also a non-trivial program added. The program gives the prime factors of a given number. This program checks majority of the grammar rules.
* The parser gives out the AST of the test cases as expected.
