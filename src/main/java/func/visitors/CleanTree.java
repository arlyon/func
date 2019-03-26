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
import java.util.HashMap;
import java.util.Map;

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
        return null;
    }

    @Override
    public AST visit(If cmd) {
        return null;
    }

    @Override
    public AST visit(While cmd) {
        return null;
    }

    @Override
    public AST visit(Read cmd) {
        return null;
    }

    @Override
    public AST visit(Write cmd) {
        return null;
    }

    @Override
    public AST visit(Expressions expressions) {
        return null;
    }

    @Override
    public AST visit(IntExpression intExpression) {
        return null;
    }

    @Override
    public AST visit(FunctionExpression functionExpression) {
        return null;
    }

    @Override
    public AST visit(Statements statements) {
        return null;
    }

    @Override
    public AST visit(Arguments arguments) {
        return null;
    }

    @Override
    public AST visit(Condition condition) {
        return null;
    }

    @Override
    public AST visit(Identifier identifier) {
        return null;
    }

    @Override
    public Method visit(Method method) {
        return method;
    }

    @Override
    public Methods visit(Methods methods) {
        Map<String, Method> methodNames = new HashMap<>();
        for (Method m : methods.methods) {
            methodNames.put(m.id.name, m);
        }
        methods.methods = new ArrayList<>(methodNames.values());
        return methods;
    }

    @Override
    public Program visit(Program program) {
        program.methods = this.visit(program.methods);
        return program;
    }
}
