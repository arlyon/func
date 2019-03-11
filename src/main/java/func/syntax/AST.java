package func.syntax;

import func.visitors.ASTVisitor;

public abstract class AST {
    public abstract <T> T accept(ASTVisitor<T> visitor);
}
