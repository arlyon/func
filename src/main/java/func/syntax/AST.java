package func.syntax;

public abstract class AST {
    public abstract <T> T accept(ASTVisitor<T> visitor);
}
