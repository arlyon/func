package func.syntax;

import func.syntax.bop.BinaryOp;
import func.syntax.exp.Expressions;
import func.visitors.ASTPrinter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Condition extends AST {
    public BinaryOp bop;
    public Expressions exps;

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
