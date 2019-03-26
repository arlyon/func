### Functools

This is the repository for a toy language called func. Func has a fairly simple grammar, consisting of basic
mathematical operations. This package comes with a parser, lexer, and multiple backends consisting of a Java
transpiler, a JVM compiler, and a MIPS assembly compiler.

#### Grammar

| Rule | Rewrite |
|:--------:| --------- |
| `<program>` | `<methods>` |
| `<methods>` | `<method>`;[`<methods>`] |
| `<method>` | method `<id>`([`<args>`])[vars `<args>`] begin `<statements>` [return `<id>`;] endmethod |
| `<args>` | `<id>`[,`<args>`]  |
| `<statements>` | `<statement>`;[`<statements>`] |
| `<statement>` | `<assign>` or `<if>` or `<while>` or `<rw>` |
| `<rw>` | read `<id>` or write `<exp>` |
| `<assign>` | `<id>` := `<exp>` |
| `<if>` | if  `<cond>` then `<statements>` [else `<statements>`] endif |
| `<while>` | while `<cond>` begin `<statements>` endwhile |
| `<cond>` | `<bop>` (`<exps>`) |
| `<bop>` | less or lessEq or eq or nEq |
| `<exps>` | `<exp>` [,`<exps>`] |
| `<exp>` | `<id>`[( `<exps>` )] | `<int>` |
| `<int>` | a natural number |
| `<id>` | any string starting with character followed by characters or numbers (that is disjoint from the keywords) |

#### Getting Started

To build the application yourself, run `./gradlew build`. This will bundle the program and its dependencies 
into `build/distributions/func-VERSION.zip`. Then, you can unzip the archive and run the program in bin. The
command line is well documented, and help can be invoked with the help parameter.

```bash
bin/func help
```

It is also possible to just run the program directly with the `run` command in gradle:

```bash
./gradlew run --args="help"
```

#### Features

There are a number of interesting features (by an amateur's standard) built into this compiler.

- Full error recovery in the parser to collect as many errors as possible.
- Rudimentary type analysis: The system infers types at compile time and ensures
    types are not violated during assignments and function calls.
- Assignment analysis: ensuring values are non-null before they are used.
- Semantic analysis that ensures:
    - Condition not updated in while loop
    - All function arguments are used
    - All function variables are used
    - All variables in a given scope exist
    - Functions do not declare more than 4 arguments
    - Variables are not declared twice in a given scope
- Some compile-time syntax tree optimizations:
    - Ahead-of-time calculation of static variables and function calls (ie `plus(10, 10) -> 20`)
    - Removal of redundant code
- Unlimited<sup>[1]</sup> Function Depth

> [1] No promises

#### Areas for further discovery

- Build an interpreter
- More useful static analysis
    - Conditional null detection
    - Inlining of variables
- Back end optimization (back patching!)
- Build a proper IR instead of going direct to ASM

#### Technology

Functools uses Gradle for building and dependency management. You can see all the tasks available to you using the
`./gradlew tasks` command. Some useful ones are `test`, `dependencies`, `run`, and `build`.

The Lexer is created using JFlex, and the command line interface using picocli. The templating used for some of the
code generation is handled by handlebars.