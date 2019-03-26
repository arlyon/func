package func.syntax;

import func.syntax.statement.Statements;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Method extends AST {
    public Identifier id;
    public Arguments args;
    public Arguments vars;
    public Statements statements;
    public Identifier ret;

    @Override
    public String toString() {
        return "Method " + this.id.name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
