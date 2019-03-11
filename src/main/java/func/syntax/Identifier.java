package func.syntax;

import func.visitors.ASTPrinter;
import func.visitors.ASTVisitor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Identifier extends AST {

    public String name;

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
