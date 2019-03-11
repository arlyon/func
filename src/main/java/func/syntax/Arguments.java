package func.syntax;

import func.visitors.ASTPrinter;
import func.visitors.ASTVisitor;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Arguments extends AST {
    public List<Identifier> identifiers;

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
