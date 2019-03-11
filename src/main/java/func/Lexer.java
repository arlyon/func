package func;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple class on top of JFlex to implement the
 * iterator interface. Also acts as an isolation
 * point between JFlex and the rest of the program.
 */
public class Lexer extends func.JFlexLexer implements Iterator<Token> {

    private Token peek;
    private boolean eof;

    /**
     * Creates a new Lexer.
     *
     * @param in the java.io.Reader to read input from.
     */
    Lexer(Reader in) {
        super(in);
        this.peek = null;
        this.eof = false;
    }

    /**
     * Increment the lexer, ignoring whitespace.
     */
    private Token lex() {
        try {
            Token ft = this.yylex();
            while (ft != null && ft.type == Token.Type.WHITESPACE) ft = this.yylex();
            if (ft.type == Token.Type.EOF) this.eof = true;
            return ft;
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(1);
            return null;
        }
    }

    @Override
    public boolean hasNext() {
        if (this.eof) return false;
        if (this.peek == null) this.peek = lex();
        return true;
    }

    @Override
    public Token next() {
        if (this.peek != null) {
            Token next = this.peek;
            this.peek = null;
            return next;
        }

        if (this.eof) throw new NoSuchElementException();
        else return lex();
    }
}
