package func.syntax.statement;

import func.syntax.ASTVisitor;
import func.syntax.Condition;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class While extends Statement {
    public Condition cond;
    public Statements statements;

    @Override
    public String toString() {
        return "while "+cond.toString();
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
