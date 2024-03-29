package func.syntax.statement;

import func.syntax.ASTVisitor;
import func.syntax.Condition;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class If extends Statement {
    public Condition cond;
    public Statements then;
    public Statements otherwise;

    @Override
    public String toString() {
        ASTPrinter p = new ASTPrinter();
        p.visit(cond);
        return "if " + p.toString();
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
