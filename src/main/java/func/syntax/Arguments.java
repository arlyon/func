package func.syntax;

import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class Arguments extends AST {
    public List<Identifier> identifiers;

    public Arguments(Identifier... idents) {
        this.identifiers = Arrays.asList(idents);
    }

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
