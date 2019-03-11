package func.syntax.statement;

import func.syntax.AST;
import func.visitors.ASTVisitor;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Statements extends AST {

    public List<Statement> statements;

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
