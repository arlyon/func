package func.syntax.statement;

import func.syntax.ASTVisitor;
import func.syntax.Condition;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class While extends Statement {
    public Condition cond;
    public Statements statements;

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
