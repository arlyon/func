package func.syntax;

import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Program extends AST {
    public Methods methods;

    @Override
    public String toString() {
        ASTPrinter p = new ASTPrinter();
        p.visit(this);
        return p.toString();
    }

    public Method mainMethod() {
        return this.methods.methods.stream().filter(x -> x.id.name.equals("main")).findFirst().orElse(null);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
