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
     * @param in the java.io.Reader to read input from.
     */
    Lexer(Reader in) {
        super(in);
        this.current = null;
    }

    @Override
    public boolean hasNext() {
        if (this.current == null) {
            try {
                this.current = this.yylex();
            } catch (IOException e) {
                return false;
            }
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
            try {
                next = this.yylex();
                if (next == null) throw new NoSuchElementException();
            } catch (IOException ignored) {
                throw new NoSuchElementException();
            }
        }

        return next;
    }
}
