package func.visitors;

import func.syntax.*;
import func.syntax.exp.Expression;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.*;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static func.Func.builtins;

/**
 * Prunes the syntax tree, removing dead code.
 * <p>
 * Currently only removes duplicate functions
 * but could be extended to remove duplicate
 * assignments or
 */
public class CleanTree implements ASTVisitor<AST> {
    @Override
    public AST visit(Assign cmd) {
        cmd.expression = (Expression) cmd.expression.accept(this);
        return cmd;
    }

    @Override
    public AST visit(If cmd) {
        cmd.then = this.visit(cmd.then);
        cmd.otherwise = this.visit(cmd.otherwise);
        return cmd;
    }

    @Override
    public AST visit(While cmd) {
        cmd.statements = this.visit(cmd.statements);
        return cmd;
    }

    @Override
    public AST visit(Read cmd) {
        return cmd;
    }

    @Override
    public AST visit(Write cmd) {
        return cmd;
    }

    @Override
    public AST visit(Expressions expressions) {
        expressions.expressions = expressions.expressions.stream()
            .map(e -> (Expression) e.accept(this))
            .collect(Collectors.toList());
        return expressions;
    }

    @Override
    public AST visit(IntExpression intExpression) {
        return intExpression;
    }

    @Override
    public Expression visit(FunctionExpression functionExpression) {
        // calculate constants
        if (functionExpression.expressions != null && builtins.contains(functionExpression.id)) {
            if (functionExpression.expressions.expressions.stream().anyMatch(x -> !(x instanceof IntExpression))) return functionExpression;
            BiFunction<Integer, Integer, Integer> op = null;
            switch (functionExpression.id.name) {
                case "plus": op = (x,y) -> x+y; break;
                case "minus": op = (x,y) -> x-y; break;
                case "times": op = (x,y) -> x*y; break;
                case "divide": op = (x,y) -> x*y; break;
            }
            return new IntExpression(op.apply(
                ((IntExpression) functionExpression.expressions.expressions.get(0)).integer,
                ((IntExpression) functionExpression.expressions.expressions.get(1)).integer
            ));
        }
        return functionExpression;
    }

    @Override
    public Statements visit(Statements statements) {
        statements.statements = statements.statements.stream()
            .map(s -> (Statement) s.accept(this))
            .collect(Collectors.toList());
        return statements;
    }

    @Override
    public AST visit(Arguments arguments) {
        return arguments;
    }

    @Override
    public Condition visit(Condition condition) {
        return condition;
    }

    @Override
    public Identifier visit(Identifier identifier) {
        return identifier;
    }

    @Override
    public Method visit(Method method) {
        if (method.statements != null)
            method.statements.statements = method.statements.statements.stream()
                .map(s -> (Statement) s.accept(this))
                .collect(Collectors.toList());
        return method;
    }

    /**
     * Cleans duplicate functions from the tree
     * and puts the main method first.
     */
    @Override
    public Methods visit(Methods methods) {
        Map<String, Method> methodNames = new HashMap<>();
        Method main = null;
        for (Method m : methods.methods) {
            if (m.id.name.equals("main")) main = m;
            else methodNames.put(m.id.name, this.visit(m));
        }

        LinkedList<Method> cleanedMethods = new LinkedList<>();
        cleanedMethods.addFirst(main);
        cleanedMethods.addAll(methodNames.values());
        methods.methods = cleanedMethods;
        return methods;
    }

    @Override
    public Program visit(Program program) {
        program.methods = this.visit(program.methods);
        return program;
    }
}
