package func.syntax.exp;

import func.SyntaxError;
import func.syntax.ASTVisitor;
import func.syntax.Identifier;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

import java.util.List;

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
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
