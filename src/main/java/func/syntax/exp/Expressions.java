package func.syntax.exp;

import func.syntax.AST;
import func.syntax.ASTVisitor;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Expressions extends AST {
    public List<Expression> expressions;

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
