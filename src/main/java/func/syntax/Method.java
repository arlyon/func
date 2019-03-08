package func.syntax;

import func.syntax.statement.Statements;
import func.visitors.ASTPrinter;
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
        ASTPrinter p = new ASTPrinter();
        p.visit(this);
        return p.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
