package func;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * The base token type for the Func programming language.
 * <p>
 * Also contains some additional data about where it was
 * encountered such as the column, row, lexeme, and length
 * of the token.
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class Token {

    public final Type type;
    public String lexeme;
    public int column;
    public int row;
    public int length;

    @Override
    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean withLocation) {
        StringBuilder builder = new StringBuilder();

        if (this.lexeme != null && this.lexeme.length() > 0) builder.append(this.lexeme);
        else builder.append(this.type);

        if (withLocation) {
            builder.append(" [").append(this.row).append(":").append(this.column);
            builder.append("-").append(this.column + this.length);
            builder.append("]");
        }
        return builder.toString();
    }

    public enum Type {
        IDENTIFIER,
        INT_LITERAL,
        ASSIGN,
        LPAR,
        RPAR,
        SEMI,
        COMMA,
        WHITESPACE,
        EOF,
        UNK,

        METHOD,
        VARS,
        BEGIN,
        RETURN,
        ENDMETHOD,
        READ,
        WRITE,
        IF,
        THEN,
        ELSE,
        ENDIF,
        WHILE,
        ENDWHILE,

        LESS,
        LESSEQ,
        EQ,
        NEQ;

        public boolean endsStatement() {
            return this == Type.RETURN ||
                this == Type.ENDMETHOD ||
                this == Type.ENDWHILE ||
                this == Type.ENDIF ||
                this == Type.ELSE ||
                this == Type.EOF;
        }
    }
}