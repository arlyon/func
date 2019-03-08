package func.syntax.statement.rw;

import func.syntax.ASTVisitor;
import func.syntax.exp.Expression;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Write extends ReadWrite {
    public Expression exp;

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
