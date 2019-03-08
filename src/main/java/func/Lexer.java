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
public class Lexer extends func.JFlexLexer implements Iterator<FileToken> {

    private FileToken current;

    /**
     * Creates a new Lexer.
     *
     * @param in the java.io.Reader to read input from.
     */
    Lexer(Reader in) {
        super(in);
        this.current = null;
    }

    /**
     * Increment the lexer, ignoring whitespace.
     */
    private FileToken lex() {
        FileToken ft = null;
        try {
            ft = this.yylex();
            while (ft != null && ft.type == Token.Type.WHITESPACE) {
                ft = this.yylex();
            }
        } catch (IOException ignore) {
        }
        return ft;
    }

    @Override
    public boolean hasNext() {
        if (this.current == null) {
            this.current = lex();
        }

        return this.current != null;
    }

    @Override
    public FileToken next() {
        FileToken next;
        if (this.current != null) {
            next = this.current;
            this.current = null;
        } else {
            next = lex();
            if (next == null) throw new NoSuchElementException();
        }

        return next;
    }
}
