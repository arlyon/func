### version 2.1.1

- fix issues with Java and JVM compile

### version 2.1.0

- rewrite the tree to pre-compute builtin operations
- rewrite the tree to move the main function first
- arbitrary function calls and recursion
- change the preprocessor to only accept 8 arguments
- fix conditionals (the were backwards!)

### version 2.0.0

- add MIPS compilation

### version 1.3.0

- add Java transpilation
- add JVM compilation

### version 1.2.0

- make program collect all parse errors
- try and parse as much as possible, returning whatever valid syntax it can
- list all syntax errors at once
- simplify syntax errors (make them more ergonomic)

### version 1.1.0

- simplify io
- support input from stdin
- replace airlift with picocli
- add validate command

### version 1.0.0

- implement lexer using JFlex
- implement recursive descent parser / AST
- implement ASTPrinter visitor (with indentation support)
- implement simple command line program format func files