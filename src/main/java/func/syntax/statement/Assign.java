package func.syntax.statement;

import func.syntax.ASTVisitor;
import func.syntax.Identifier;
import func.syntax.exp.Expression;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Assign extends Statement {

    public Identifier id;
    public Expression expression;

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
