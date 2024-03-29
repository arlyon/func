package func.syntax;

import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Methods extends AST {
    public List<Method> methods;

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
