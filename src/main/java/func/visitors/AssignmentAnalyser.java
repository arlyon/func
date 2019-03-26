package func.visitors;

import func.errors.SemanticError;
import func.syntax.*;
import func.syntax.exp.Expression;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.*;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;

import java.util.*;
import java.util.stream.Collectors;

import static func.Func.builtins;

/**
 * Analyses the syntax tree and finds all the places where vars are used before they are assigned to.
 */
public class AssignmentAnalyser implements ASTVisitor<Boolean> {

    private final List<Identifier> globalScope = new ArrayList<>(builtins);
    private Set<Identifier> assigned;
    private final List<SemanticError> errors = new LinkedList<>();

    private SemanticError error(String message, AST... nodes) {
        SemanticError e = new SemanticError(message, nodes);
        this.errors.add(e);
        return e;
    }

    public List<SemanticError> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    @Override
    public Boolean visit(Assign cmd) {
        if (cmd.expression instanceof FunctionExpression) {
            FunctionExpression f = (FunctionExpression) cmd.expression;
            if (cmd.id.equals(f.id) && f.expressions == null) {
                error("Variable assigned to itself", cmd);
            }
        }
        cmd.expression.accept(this);
        assigned.add(cmd.id);
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(If cmd) {
        // todo detect conditional definition
        cmd.cond.accept(this);
        cmd.then.accept(this);
        cmd.otherwise.accept(this);
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(While cmd) {
        // todo detect conditional definition
        cmd.cond.accept(this);
        cmd.statements.accept(this);
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Read cmd) {
        this.assigned.add(cmd.id);
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Write cmd) {
        cmd.exp.accept(this);
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Expressions expressions) {
        for (Expression exp : expressions.expressions)
            exp.accept(this);

        return errors.isEmpty();
    }

    @Override
    public Boolean visit(IntExpression intExpression) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(FunctionExpression functionExpression) {
        if (!assigned.contains(functionExpression.id))
            error("Identifier used before being assigned", functionExpression.id);
        if (functionExpression.expressions != null)
            functionExpression.expressions.accept(this);
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Statements statements) {
        statements.statements.forEach(v -> v.accept(this));
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Arguments arguments) {
        arguments.identifiers.forEach(this::visit);
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Condition condition) {
        condition.exps.accept(this);
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Identifier identifier) {
        if (!assigned.contains(identifier)) error("Identifier used before being assigned", identifier);
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Method method) {
        this.assigned = new HashSet<>(globalScope);
        if (method.args != null) assigned.addAll(method.args.identifiers);

        if (method.statements != null)
            for (Statement s : method.statements.statements) {
                s.accept(this);
            }

        if (method.ret != null)
            method.ret.accept(this);

        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Methods methods) {
        this.globalScope.addAll(methods.methods.stream().map(method -> method.id).collect(Collectors.toList()));
        return methods.methods.stream().allMatch(m -> m.accept(this));
    }

    @Override
    public Boolean visit(Program program) {
        return program.methods.accept(this);
    }
}
