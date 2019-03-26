package func.syntax;

import func.syntax.statement.Statements;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class Method extends AST {
    public Identifier id;
    public Arguments args;
    public Arguments vars;
    public Statements statements;
    public Identifier ret;

    @Override
    public String toString() {
        return "Method " + this.id.name;
    }

    public List<Identifier> variables() {
        List<Identifier> idents = new LinkedList<>();
        if (this.args != null) idents.addAll(this.args.identifiers);
        if (this.vars != null) idents.addAll(this.vars.identifiers);
        return idents;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
