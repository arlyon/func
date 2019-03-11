package func;

public class MatchError extends SyntaxError {
    public MatchError(String message, Token... token) {
        super(message, token);
    }
}
