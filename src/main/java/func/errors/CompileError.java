package func.errors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CompileError extends RuntimeException {
    public final String message;
}
