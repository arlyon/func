package func.syntax.bop;

import func.syntax.AST;
import func.visitors.ASTPrinter;

public abstract class BinaryOp extends AST {

    @Override
    public String toString() {
        ASTPrinter p = new ASTPrinter();
        p.visit(this);
        return p.toString();
    }
}
