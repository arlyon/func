package func.errors;

import func.syntax.AST;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SemanticError extends CompileError {
    private final AST[] nodes;

    public SemanticError(String message, AST... nodes) {
        super(message);
        this.nodes = nodes;
    }

    public String toString() {
        String out = "";
        out += this.message;
        out += " " + Arrays.stream(this.nodes).map(AST::toString).collect(Collectors.joining(", "));
        return out;
    }
}
