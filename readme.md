### Functools

This is the repository for a toy language called func. Func has a fairly simple grammar, consisting of basic
mathematical operations. Currently only the AST is implemented (alongside an indentation-friendly code prettifier).

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

#### Technology

Functools uses Gradle for building and dependency management. You can see all the tasks available to you using the
`./gradlew tasks` command. Some useful ones are `test`, `dependencies`, `run`, and `build`.

The Lexer is created using JFlex, and the command line interface using Airline.