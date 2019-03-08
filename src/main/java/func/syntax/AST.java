package func.syntax;

public abstract class AST {
    public abstract void accept(ASTVisitor visitor);
}
