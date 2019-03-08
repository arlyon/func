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
        p.visit(this);
        return p.toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
