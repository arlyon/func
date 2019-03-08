package func;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * The base token type for the Func programming language.
 * <p>
 * Also contains some additional data about where it was
 * encountered such as the column, row, value, and length
 * of the token.
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class Token {

    public final Type type;
    String value;
    int column;
    int row;
    int length;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
            .append(this.type).append(" ");
        if (this.value != null) builder.append(this.value);
        builder.append(" [").append(this.row).append(":").append(this.column);
        builder.append("-").append(this.column + this.length);
        builder.append("]");
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
        NEQ
    }
}