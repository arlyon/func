package func;

import func.syntax.*;
import func.syntax.bop.*;
import func.syntax.exp.Expression;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.*;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;

import java.util.*;

/**
 * Parses a {@link Token} iterator building a
 * valid AST for the Func language. Any syntax errors
 * will raise a BadSyntax error with an appropriate message.
 */
public class Parser {

    private final List<Token> tokens;
    private final ListIterator<Token> iterator;

    public Parser(Iterator<Token> tokens) {
        this.tokens = new ArrayList<>();
        tokens.forEachRemaining(this.tokens::add);
        this.iterator = this.tokens.listIterator();
    }

    /**
     * Gives a list of token types, takes them in that order
     * from the token iterator and asserts they are of the
     * correct type.
     */
    private void take(Token.Type... tokens) throws BadSyntax {
        Token actual;
        for (Token.Type expected : tokens) {
            actual = this.iterator.next();
            if (actual.type != expected) {
                this.iterator.previous();
                throw new BadSyntax("Expected " + expected + " but got", actual);
            }
        }
    }

    /**
     * Signifies an optional part of the grammar using an HOF.
     * <p>
     * For example:
     * {@code take(Token.Type.LPAR); optional(() -> take(Token.Type.COMMA)); take(Token.Type.RPAR)}
     * matches both {@code (,)} and {@code ()}.
     */
    private <T> T optional(OptionalSyntax<T> func) {
        try {
            return func.apply();
        } catch (BadSyntax e) {
            return null;
        }
    }

    /**
     * Defines the type of functions that can be used in the optional HOF.
     */
    @FunctionalInterface
    public interface OptionalSyntax<T> {
        T apply() throws BadSyntax;
    }

    public Program program() throws BadSyntax {
        Methods methods = this.methods();

        Optional<Method> main = methods.methods.stream().filter(method -> method.id.name.equals("main")).findFirst();
        if (!main.isPresent())
            throw new BadSyntax("Program must have a main function.");

        Method m = main.get();
        if (m.args != null || m.ret != null) {
            throw new BadSyntax("Main function must have no arguments or return value.");
        }

        return new Program(methods);
    }

    private Methods methods() throws BadSyntax {
        List<Method> methods = new ArrayList<>();
        while (this.iterator.hasNext()) methods.add(this.method());
        return new Methods(methods);
    }

    private Method method() throws BadSyntax {
        this.take(Token.Type.METHOD);
        Identifier id = this.identifier();

        this.take(Token.Type.LPAR);
        Arguments args = this.optional(this::arguments);
        this.take(Token.Type.RPAR);

        Arguments vars = this.optional(this::vars);

        this.take(Token.Type.BEGIN);
        Statements statements = this.statements();
        Identifier ret = this.optional(this::ret);
        this.take(Token.Type.ENDMETHOD, Token.Type.SEMI);

        return new Method(id, args, vars, statements, ret);
    }

    private Identifier ret() throws BadSyntax {
        this.take(Token.Type.RETURN);
        Identifier out = this.identifier();
        this.take(Token.Type.SEMI);
        return out;
    }

    private Arguments vars() throws BadSyntax {
        this.take(Token.Type.VARS);
        return this.arguments();
    }

    private Statements statements() throws BadSyntax {
        List<Statement> statements = new ArrayList<>();
        try {
            while (this.iterator.hasNext()) {
                statements.add(this.statement());
                this.take(Token.Type.SEMI);
            }
        } catch (BadSyntax err) {
            if (statements.isEmpty()) throw err;
        }
        return new Statements(statements);
    }

    private Statement statement() throws BadSyntax {
        Token next = this.iterator.next();
        Token.Type type = next.type;
        this.iterator.previous();
        Condition cond;

        switch (type) {
            case READ:
                this.take(Token.Type.READ);
                return new Read(this.identifier());
            case WRITE:
                this.take(Token.Type.WRITE);
                return new Write(this.expression());
            case IDENTIFIER:
                Identifier i = this.identifier();
                this.take(Token.Type.ASSIGN);
                Expression exp = this.expression();
                return new Assign(i, exp);
            case IF:
                this.take(Token.Type.IF);
                cond = this.cond();
                this.take(Token.Type.THEN);
                Statements then = this.statements();
                Statements otherwise = this.optional(this::otherwise);
                this.take(Token.Type.ENDIF);
                return new If(cond, then, otherwise);
            case WHILE:
                this.take(Token.Type.WHILE);
                cond = this.cond();
                this.take(Token.Type.BEGIN);
                Statements whil = this.statements();
                this.take(Token.Type.ENDWHILE);
                return new While(cond, whil);
            default:
                throw new BadSyntax("Not a valid statement", next);
        }
    }

    private Statements otherwise() throws BadSyntax {
        this.take(Token.Type.ELSE);
        return this.statements();
    }

    private Condition cond() throws BadSyntax {
        BinaryOp bop = this.bop();
        this.take(Token.Type.LPAR);
        Expressions exps = this.expressions();
        this.take(Token.Type.RPAR);
        return new Condition(bop, exps);
    }

    private BinaryOp bop() throws BadSyntax {
        switch (this.iterator.next().type) {
            case LESS:
                return new Less();
            case LESSEQ:
                return new LessEq();
            case EQ:
                return new Eq();
            case NEQ:
                return new NEq();
            default:
                throw new BadSyntax("BAD BAD BAD");
        }
    }

    private Expression expression() throws BadSyntax {
        Token s = this.iterator.next();
        Token.Type t = s.type;
        this.iterator.previous();
        switch (t) {
            case IDENTIFIER:
                return this.functionExpression();
            case INT_LITERAL:
                return this.intExpression();
        }
        return null;
    }

    private FunctionExpression functionExpression() throws BadSyntax {
        Identifier x = this.identifier();
        Expressions exps = this.optional(this::functionApplication);
        return new FunctionExpression(x, exps);
    }

    private Expressions functionApplication() throws BadSyntax {
        this.take(Token.Type.LPAR);
        Expressions e = this.expressions();
        this.take(Token.Type.RPAR);
        return e;
    }

    private Expressions expressions() throws BadSyntax {
        List<Expression> exps = new ArrayList<>();
        try {
            while (this.iterator.hasNext()) {
                exps.add(this.expression());
                this.take(Token.Type.COMMA);
            }
        } catch (BadSyntax badSyntax) {
            if (exps.isEmpty()) throw badSyntax;
        }
        return new Expressions(exps);
    }

    private IntExpression intExpression() throws BadSyntax {
        Token t = this.iterator.next();
        if (t.type != Token.Type.INT_LITERAL) {
            throw new BadSyntax("Expected int, not ", t);
        }
        return new IntExpression(Integer.parseInt(t.value));
    }

    private Arguments arguments() throws BadSyntax {
        List<Identifier> identifiers = new LinkedList<>();
        while (this.iterator.hasNext()) {
            identifiers.add(this.identifier());
            if (this.iterator.next().type == Token.Type.COMMA) continue;
            this.iterator.previous();
            break;
        }
        return new Arguments(identifiers);
    }

    private Identifier identifier() throws BadSyntax {
        Token token = this.iterator.next();
        if (token.type != Token.Type.IDENTIFIER) {
            this.iterator.previous();
            throw new BadSyntax("Expected identifier, not ", token);
        }
        return new Identifier(token.value);
    }
}
