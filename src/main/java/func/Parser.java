package func;

import func.errors.MatchError;
import func.errors.SyntaxError;
import func.syntax.*;
import func.syntax.exp.Expression;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.*;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;

import java.util.*;

import static func.Token.Type.*;

/**
 * Parses a {@link Token} iterator building a
 * valid AST for the Func language. Any syntax errors
 * will raise a {@link SyntaxError} with an appropriate message.
 */
public class Parser {

    private final List<Token> tokens;
    private final ListIterator<Token> iterator;
    private final List<SyntaxError> errors;

    public Parser(Iterator<Token> tokens) {
        this.tokens = new ArrayList<>();
        tokens.forEachRemaining(
            this.tokens::add
        );
        this.iterator = this.tokens.listIterator();
        errors = new LinkedList<>();
    }

    private SyntaxError error(String message, Token... token) {
        SyntaxError e = new SyntaxError(message, token);
        this.errors.add(e);
        return e;
    }

    public List<SyntaxError> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    /**
     * Gives a list of token types, takes them in that order
     * from the token iterator and asserts they are of the
     * correct type.
     */
    private void take(Token.Type... tokens) throws SyntaxError {
        Token actual;
        for (Token.Type expected : tokens) {
            if (!this.iterator.hasNext()) {
                throw new SyntaxError("Reached " + peekBack() + " when expecting " + expected, peekBack());
            }
            actual = this.iterator.next();
            if (actual.type != expected) {
                this.iterator.previous();
                throw new SyntaxError("Expected " + expected + ", not", actual);
            }
        }
    }

    /**
     * A wrapper around take that takes the provided
     * tokens and raises a match error if they do not
     * exist. Match is used at the beginning of optional
     * clauses. For example, a syntax error on the var token
     * means that vars isn't there, but a syntax error on the
     * arguments in the vars is a normal syntax error.
     */
    private void match(Token.Type... tokens) throws MatchError {
        try {
            take(tokens);
        } catch (SyntaxError e) {
            throw new MatchError("Does not match!", e.token);
        }
    }

    /**
     * Peeks ahead of the iterator, staying in place.
     */
    private Token peek() {
        if (!this.iterator.hasNext()) return null;
        Token match = this.iterator.next();
        this.iterator.previous();
        return match;
    }

    /**
     * Peeks behind the iterator, staying in place.
     */
    private Token peekBack() {
        Token match = this.iterator.previous();
        this.iterator.next();
        return match;
    }

    /**
     * Signifies an optional "unit" of the grammar using an HOF.
     * This allows you to group logical parts of the grammar together,
     * such as the "vars" which is a token combined with a list of args.
     * <p>
     * Allows the sub-parts of the grammar to throw exceptions without
     * worrying about how they are handled.
     */
    private <T> T optional(OptionalSyntax<T> func) {
        try {
            return func.apply();
        } catch (MatchError e) {
            return null;
        }
    }

    /**
     * Signifies a required "unit" of the grammar using an HOF.
     * This allows you to group logical parts of the grammar together,
     * logging errors, and attempting to continue on.
     * <p>
     * Allows the sub-parts of the grammar to throw exceptions without
     * worrying about how they are handled.
     */
    private <T> T required(OptionalSyntax<T> func) {
        try {
            return func.apply();
        } catch (SyntaxError e) {
            error(e.message, e.token);
            return null;
        }
    }

    @FunctionalInterface
    public interface OptionalSyntax<T> {
        T apply() throws SyntaxError;

    }

    private void fastForward(Token.Type token) {
        fastForward(token, null);
    }

    /**
     * Fast-forwards to the given token and throws an error
     * if it has to skip over any tokens.
     * <p>
     * It is used to return the program to a known state in
     * when things go wrong. Also supports setting a "before"
     * token which can be used to signal a missing token.
     * For example, finding an RPAR before an LPAR probably
     * means we've forgotten an LPAR.
     */
    private void fastForward(Token.Type until, Token.Type before) {
        Token t;
        List<Token> errors = new ArrayList<>();
        boolean missingToken = false;

        while (iterator.hasNext()) {
            t = iterator.next();
            if (Objects.equals(until, t.type)) {
                if (!errors.isEmpty()) {
                    error("Expected " + until + ", not", errors.toArray(new Token[]{}));
                }
                break;
            } else if (t.type == before) {
                missingToken = true;
                break;
            }
            errors.add(t);
        }

        missingToken |= !iterator.hasNext();

        if (missingToken) {
            // we have reached the end of the program without the expected symbol
            if (!errors.isEmpty())
                throw new SyntaxError("Bad Syntax. Expected a missing " + until + ".", errors.toArray(new Token[]{}));
            throw new SyntaxError("Bad Syntax. Expected a missing " + until + ".");
        }
        this.iterator.previous();
    }

    /**
     * Parses a program. A program is a list of methods with the
     * assertion that there is at least one main method with no
     * arguments or return values.
     */
    public Program program() {
        Methods methods = this.methods();
        return new Program(methods);
    }

    private Methods methods() {
        List<Method> methods = new ArrayList<>();
        while (this.iterator.hasNext() && peek().type != EOF) {
            methods.add(this.method());
        }
        return new Methods(methods);
    }

    public Method method() {
        this.take(Token.Type.METHOD);
        Identifier id = required(this::identifier);
        Arguments args = null;

        try {
            fastForward(LPAR, RPAR);
            this.take(LPAR);
            args = optional(this::arguments);
            try {
                fastForward(RPAR);
                this.take(RPAR);
            } catch (SyntaxError e) {
                error(e.message, e.token);
            }
        } catch (SyntaxError e) {
            error(e.message, e.token);
        }

        Arguments vars = optional(this::vars);

        try {
            this.take(BEGIN);
        } catch (SyntaxError e) {
            error(e.message, e.token);
        }

        Statements statements = required(this::statements);
        Identifier ret = optional(this::ret);

        try {
            this.take(Token.Type.ENDMETHOD, Token.Type.SEMI);
        } catch (SyntaxError e) {
            error(e.message, e.token);
        }

        return new Method(id, args, vars, statements, ret);
    }

    private Identifier ret() throws SyntaxError {
        this.match(Token.Type.RETURN);
        Identifier out = required(this::identifier);
        this.take(Token.Type.SEMI);
        return out;
    }

    public Arguments vars() throws MatchError {
        this.match(Token.Type.VARS);
        Arguments vars = required(this::arguments);
        fastForward(BEGIN);
        return vars;
    }

    public Statements statements() throws SyntaxError {
        List<Statement> statements = new ArrayList<>();
        while (this.iterator.hasNext()) {
            try {
                statements.add(this.statement());
            } catch (SyntaxError e) {
                boolean unrecognisedStatement = fastForwardStatement();
                if (unrecognisedStatement) {
                    statements.add(null);
                } else {
                    break;
                }
            }
            try {
                take(SEMI);
            } catch (SyntaxError e) {
                error(e.message, e.token);
            }
        }
        if (statements.isEmpty()) throw error("No statements", this.iterator.hasNext() ? this.peekBack() : null);
        return new Statements(statements);
    }

    /**
     * Fast-forwards over a given statement to get back to a known state.
     *
     * @return true if an unrecognized statement was discovered
     */
    private boolean fastForwardStatement() {
        List<Token> errors = new ArrayList<>();
        do {
            Token t = this.iterator.next();
            if (t.type.endsStatement()) break;
            if (t.type == Token.Type.SEMI) {
                if (!errors.isEmpty())
                    error("Unrecognised statement", errors.toArray(new Token[]{}));
                break;
            }
            errors.add(t);
        } while (iterator.hasNext());
        this.iterator.previous();
        return !errors.isEmpty();
    }

    public Statement statement() throws MatchError {
        Token next = peek();

        try {
            switch (next.type) {
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
                    this.take(IF);
                    Condition ifCond = this.cond();
                    this.take(THEN);
                    Statements then = this.statements();
                    Statements otherwise = this.optional(this::otherwise);
                    fastForward(ENDIF);
                    this.take(ENDIF);
                    return new If(ifCond, then, otherwise);
                case WHILE:
                    this.take(WHILE);
                    Condition whileCond = this.cond();
                    this.take(BEGIN);
                    Statements whileStatements = this.statements();
                    this.take(ENDWHILE);
                    return new While(whileCond, whileStatements);
            }
        } catch (SyntaxError e) {
            throw error(e.message, e.token);
        }

        throw new MatchError("Not a valid statement! ", next);
    }

    private Statements otherwise() throws SyntaxError {
        this.match(Token.Type.ELSE);
        return this.statements();
    }

    public Condition cond() throws SyntaxError {
        BinaryOp bop = null;
        try {
            bop = this.bop();
        } catch (MatchError e) {
            error(e.message, e.token);
        }
        try {
            fastForward(LPAR, RPAR);
            this.take(LPAR);
        } catch (SyntaxError e) {
            error(e.message, e.token);
        }
        Expressions exps = this.expressions();
        try {
            this.fastForward(RPAR);
            this.take(RPAR);
        } catch (SyntaxError e) {
            error(e.message, e.token);
        }
        return new Condition(bop, exps);
    }

    public BinaryOp bop() throws SyntaxError {
        switch (this.iterator.next().type) {
            case LESS:
                return BinaryOp.Less;
            case LESSEQ:
                return BinaryOp.LessEq;
            case EQ:
                return BinaryOp.Eq;
            case NEQ:
                return BinaryOp.NEq;
            default:
                throw new MatchError("Unrecognised binary operator", iterator.previous());
        }
    }

    public Expression expression() throws SyntaxError {
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

    private FunctionExpression functionExpression() throws SyntaxError {
        Identifier x = this.identifier();
        Expressions exps = this.optional(this::functionApplication);
        return new FunctionExpression(x, exps);
    }

    private Expressions functionApplication() throws SyntaxError {
        this.match(LPAR);
        Expressions e = this.expressions();
        this.take(Token.Type.RPAR);
        return e;
    }

    private Expressions expressions() {
        List<Expression> exps = new ArrayList<>();
        while (this.iterator.hasNext()) {
            Expression e = this.expression();
            if (e == null) break;
            exps.add(e);
            if (this.iterator.next().type == Token.Type.COMMA) continue;
            this.iterator.previous();
            break;
        }
        return new Expressions(exps);
    }

    private IntExpression intExpression() {
        Token t = this.iterator.next();
        if (t.type != INT_LITERAL) {
            throw error("Expected " + INT_LITERAL + ", not", t);
        }
        return new IntExpression(Integer.parseInt(t.lexeme));
    }

    public Arguments arguments() throws SyntaxError {
        List<Identifier> identifiers = new LinkedList<>();
        try {
            identifiers.add(this.identifier());
        } catch (SyntaxError e) {
            throw new MatchError(e.message, e.token);
        }

        while (this.iterator.hasNext() && this.peek().type == Token.Type.COMMA) {
            this.iterator.next();
            identifiers.add(required(this::identifier));
        }

        return new Arguments(identifiers);
    }

    private Identifier identifier() throws MatchError {
        Token token = this.iterator.next();
        if (token.type != Token.Type.IDENTIFIER) {
            this.iterator.previous();
            throw new MatchError("Expected " + IDENTIFIER + ", not", token);
        }
        return new Identifier(token.lexeme);
    }
}
