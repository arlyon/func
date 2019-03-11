package func.syntax.bop;

import func.visitors.ASTVisitor;

public class Eq extends BinaryOp {
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
