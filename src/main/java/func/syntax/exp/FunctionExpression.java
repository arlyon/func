package func.syntax.exp;

import func.visitors.ASTVisitor;
import func.syntax.Identifier;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FunctionExpression extends Expression {
    public Identifier id;
    public Expressions expressions;

    @Override
    public String toString() {
        ASTPrinter p = new ASTPrinter();
        p.visit(this);
        return p.toString();
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
