package func;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor

public class SyntaxError extends RuntimeException {
    public final String message;
    public Token[] token;

    public SyntaxError(String message, Token... tokens) {
        this.message = message;
        this.token = tokens;
    }

    @Override
    public String toString() {
        String out = "";
        boolean printToken = this.token != null && this.token.length > 0 && this.token[0] != null;

        if (printToken) {
            out += "[" + this.token[0].row + ":";
            out += Arrays.stream(this.token).map(x->x.column).min(Integer::compareTo).orElse(-1) + "-";
            out += Arrays.stream(this.token).map(x->x.column+x.length).max(Integer::compareTo).orElse(-1) + "] ";
        }

        out += this.message;

        if (printToken) {
            out += " " + Arrays.stream(this.token).map(x -> x.toString(false)).collect(Collectors.joining(""));
        }

        return out;
    }
}
