package func;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class BadSyntax extends Exception {
    private final String message;
    private Token token;

    @Override
    public String toString() {
        return this.message + (this.token != null ? ": " + this.token.toString() : "");
    }
}
