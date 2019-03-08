package func.syntax.bop;

import func.syntax.ASTVisitor;

public class NEq extends BinaryOp {

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
