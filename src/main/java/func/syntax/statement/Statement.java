package func.syntax.statement;

import func.syntax.AST;
import func.syntax.ASTVisitor;

public abstract class Statement extends AST {

    @Override
    public abstract void accept(ASTVisitor visitor);
}
