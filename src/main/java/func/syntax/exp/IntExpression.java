package func.syntax.exp;

import func.syntax.ASTVisitor;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IntExpression extends Expression {
    public Integer integer;

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
