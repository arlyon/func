package func;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public class Token {

    public final Type type;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
            .append(this.type).append(" ");
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