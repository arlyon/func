package func.visitors;

import func.syntax.*;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.Assign;
import func.syntax.statement.If;
import func.syntax.statement.Statements;
import func.syntax.statement.While;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gets all the identifiers from a syntax node.
 */
public class ExtractIdentifiers implements ASTVisitor<List<Identifier>> {
    @Override
    public List<Identifier> visit(Assign cmd) {
        ArrayList<Identifier> list = new ArrayList<>();
        list.addAll(cmd.id.accept(this));
        list.addAll(cmd.expression.accept(this));
        return list;
    }

    @Override
    public List<Identifier> visit(If cmd) {
        ArrayList<Identifier> list = new ArrayList<>();
        list.addAll(cmd.cond.accept(this));
        list.addAll(cmd.then.accept(this));
        list.addAll(cmd.otherwise.accept(this));
        return list;
    }

    @Override
    public List<Identifier> visit(While cmd) {
        ArrayList<Identifier> list = new ArrayList<>();
        list.addAll(cmd.cond.accept(this));
        list.addAll(cmd.statements.accept(this));
        return list;
    }

    @Override
    public List<Identifier> visit(Read cmd) {
        return new ArrayList<>(cmd.id.accept(this));
    }

    @Override
    public List<Identifier> visit(Write cmd) {
        return new ArrayList<>(cmd.exp.accept(this));
    }

    @Override
    public List<Identifier> visit(Expressions expressions) {
        return expressions.expressions.stream()
            .flatMap(expression -> expression.accept(this).stream())
            .collect(Collectors.toList());
    }

    @Override
    public List<Identifier> visit(IntExpression intExpression) {
        return new ArrayList<>();
    }

    @Override
    public List<Identifier> visit(FunctionExpression functionExpression) {
        ArrayList<Identifier> list = new ArrayList<>();
        list.addAll(functionExpression.id.accept(this));
        if (functionExpression.expressions != null) {
            list.addAll(functionExpression.expressions.accept(this));
        }
        return list;
    }

    @Override
    public List<Identifier> visit(Statements statements) {
        return statements.statements.stream().flatMap(statement -> statement.accept(this).stream()).collect(Collectors.toList());
    }

    @Override
    public List<Identifier> visit(Arguments arguments) {
        return arguments.identifiers;
    }

    @Override
    public List<Identifier> visit(Condition condition) {
        return condition.exps.expressions.stream().flatMap(expression -> expression.accept(this).stream()).collect(Collectors.toList());
    }

    @Override
    public List<Identifier> visit(Identifier identifier) {
        ArrayList<Identifier> idents = new ArrayList<>();
        idents.add(identifier);
        return idents;
    }

    @Override
    public List<Identifier> visit(Method method) {
        return null;
    }

    @Override
    public List<Identifier> visit(Methods methods) {
        return null;
    }

    @Override
    public List<Identifier> visit(Program program) {
        return null;
    }
}
