package func.visitors;

import func.errors.SemanticError;
import func.syntax.*;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.*;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static func.Func.builtins;


public class SemanticAnalyser implements ASTVisitor<Boolean> {

    private final List<SemanticError> errors;

    public SemanticAnalyser() {
        this.errors = new LinkedList<>();
    }

    private SemanticError error(String message, AST... nodes) {
        SemanticError e = new SemanticError(message, nodes);
        this.errors.add(e);
        return e;
    }

    private final List<Identifier> globalScope = new ArrayList<>(builtins);

    public List<SemanticError> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    @Override
    public Boolean visit(Assign cmd) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(If cmd) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(While cmd) {
        ExtractIdentifiers extractIdentifiers = new ExtractIdentifiers();
        List<Identifier> conditionalVariables = extractIdentifiers.visit(cmd.cond);
        List<Identifier> updatedVariables = cmd.statements.statements.stream()
            .filter(o -> o instanceof Assign)
            .map(x -> (Assign) x)
            .map(x -> x.id)
            .collect(Collectors.toList());

        if (conditionalVariables.stream().noneMatch(updatedVariables::contains))
            error("Condition not updated in loop: ", cmd.cond);

        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Read cmd) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Write cmd) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Expressions expressions) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(IntExpression intExpression) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(FunctionExpression functionExpression) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Statements statements) {
        return statements.statements.stream().allMatch(statement -> statement.accept(this));
    }

    @Override
    public Boolean visit(Arguments arguments) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Condition condition) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Identifier identifier) {
        return errors.isEmpty();
    }

    @Override
    public Boolean visit(Method method) {
        ExtractIdentifiers extractIdentifiers = new ExtractIdentifiers();
        List<Identifier> usedIdentifiers = method.statements.statements.stream()
            .flatMap(statement -> statement.accept(extractIdentifiers).stream())
            .collect(Collectors.toList());

        if (method.ret != null)
            usedIdentifiers.addAll(method.ret.accept(extractIdentifiers));

        List<Identifier> declaredIdentifiers = new LinkedList<>();
        if (method.args != null) declaredIdentifiers.addAll(method.args.accept(extractIdentifiers));
        if (method.vars != null) declaredIdentifiers.addAll(method.vars.accept(extractIdentifiers));

        for (Identifier used : usedIdentifiers) {
            if (Stream.of(declaredIdentifiers, globalScope)
                .flatMap(List::stream)
                .noneMatch(used::equals)) {
                error("Identifier doesn't exist in scope.", method, used);
            }
        }

        for (Identifier declared : declaredIdentifiers) {
            if (usedIdentifiers.stream().noneMatch(declared::equals))
                error("Identifier is not used in scope", method, declared);
        }

        if (method.vars != null && method.args != null) {
            Set<Identifier> intersection = new HashSet<>(method.vars.identifiers);
            intersection.retainAll(method.args.identifiers);
            if (!intersection.isEmpty()) {
                List<AST> elems = new LinkedList<>();
                elems.add(method);
                elems.addAll(intersection);
                error("Must not declare the same identifier in both the args and the variables.", elems.toArray(new AST[]{}));
            }
        }

        if (method.args != null && method.args.identifiers.size() > 4) {
            error("Cannot declare functions with more than 4 arguments.", method);
        }

        if (method.vars != null && method.vars.identifiers.size() > 8) {
            error("Cannot declare functions with more than 8 variables.", method);
        }

        this.visit(method.statements);

        return this.errors.isEmpty();
    }

    @Override
    public Boolean visit(Methods methods) {
        this.globalScope.addAll(methods.methods.stream().map(method -> method.id).collect(Collectors.toList()));
        return methods.methods.stream().allMatch(this::visit);
    }

    @Override
    public Boolean visit(Program program) {
        this.visit(program.methods);
        Optional<Method> main = program.methods.methods.stream().filter(method -> method.id.name.equals("main")).findFirst();
        if (main.isPresent()) {
            Method m = main.get();
            if (m.args != null)
                error("Main function must have no arguments.", m.args);
            if (m.ret != null)
                error("Main function must have no return value.", m.ret);
        } else {
            error("Program must have a main function.");
        }

        return errors.isEmpty();
    }
}
