package func.syntax.bop;

import func.syntax.ASTVisitor;

public class Eq extends BinaryOp {
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
