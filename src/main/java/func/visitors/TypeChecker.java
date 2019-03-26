package func.visitors;

import func.syntax.*;
import func.syntax.exp.Expression;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.Assign;
import func.syntax.statement.If;
import func.syntax.statement.Statements;
import func.syntax.statement.While;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;
import func.errors.SemanticError;

import java.util.*;

/**
 * Ensures that function pointers are not assigned
 * to variables and also that function calls have
 * the correct number of arguments.
 */
public class TypeChecker implements ASTVisitor<Void> {

    private ReferenceFrame frame;

    private class ReferenceFrame {

        Map<Identifier, AST> variables = new HashMap<>();
        ReferenceFrame parent = null;

        public ReferenceFrame() {
        }

        public void register(Identifier variable, AST references) {
            variables.put(variable, references);
        }


        public boolean exists(Identifier variable) {
            return variables.containsKey(variable);
        }

        public AST find(Identifier variable) {
            AST v = variables.get(variable);
            if (v != null) return v;
            if (parent != null) return parent.find(variable);
            return null;
        }

        public ReferenceFrame push() {
            ReferenceFrame r = new ReferenceFrame();
            r.parent = this;
            return r;
        }

        public ReferenceFrame pop() {
            return this.parent;
        }
    }

    private List<SemanticError> errors = new LinkedList<>();

    private SemanticError error(String message, AST... nodes) {
        SemanticError e = new SemanticError(message, nodes);
        this.errors.add(e);
        return e;
    }

    public List<SemanticError> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    /**
     * Checks assignments to ensure that:
     * - the source and destination types are the same
     * - function calls are being done to function types
     * - function calls are receiving the right number of args
     */
    @Override
    public Void visit(Assign cmd) {
        cmd.expression.accept(this);
        AST srcType = null;

        if (cmd.expression instanceof FunctionExpression) {
            FunctionExpression fe = (FunctionExpression) cmd.expression;
            if (fe.expressions != null) {
                // check function arguments
                Method calling = (Method) frame.find(fe.id);
                if (fe.expressions.expressions.size() != calling.args.identifiers.size())
                    error("Attempting to call function with " + fe.expressions.expressions.size() + " arguments, expected " + calling.args.identifiers.size() + ":", calling);

                // check types are ints
                for (Expression e : fe.expressions.expressions) {
                    if (e instanceof FunctionExpression) {
                        FunctionExpression funcArg = (FunctionExpression) e;
                        if (funcArg.expressions == null && !(frame.find(funcArg.id) instanceof IntExpression)) {
                            error("Functions may only accept int types", calling, funcArg);
                        }
                    }
                }

                // the type of the called function is an int
                srcType = new IntExpression(-1);
            } else {
                srcType = frame.find(fe.id);
            }
        } else if (cmd.expression instanceof IntExpression) {
            srcType = cmd.expression;
        }

        AST destType = frame.find(cmd.id);
        if (destType != null && srcType.getClass() != destType.getClass()) {
            error("Assigning wrong type to variable:", cmd.id, srcType);
        }
        frame.register(cmd.id, srcType);
        return null;
    }

    @Override
    public Void visit(If cmd) {
        cmd.then.accept(this);
        cmd.otherwise.accept(this);
        return null;
    }

    @Override
    public Void visit(While cmd) {
        cmd.statements.accept(this);
        return null;
    }

    @Override
    public Void visit(Read cmd) {
        return null;
    }

    @Override
    public Void visit(Write cmd) {
        return null;
    }

    @Override
    public Void visit(Expressions expressions) {
        return null;
    }

    @Override
    public Void visit(IntExpression intExpression) {
        return null;
    }

    @Override
    public Void visit(FunctionExpression functionExpression) {
        if (functionExpression.expressions != null) {
            AST ast = frame.find(functionExpression.id);
            if (!(ast instanceof Method)) {
                // calling a non-method variable!
            } else {
                Method m = (Method) ast;
                if (functionExpression.expressions.expressions.size() != m.args.identifiers.size()) {
                    // differing arguments!
                }
            }
        }


        return null;
    }

    @Override
    public Void visit(Statements statements) {
        statements.statements.forEach(s -> s.accept(this));
        return null;
    }

    @Override
    public Void visit(Arguments arguments) {
        return null;
    }

    @Override
    public Void visit(Condition condition) {
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        return null;
    }

    @Override
    public Void visit(Method method) {
        this.frame = this.frame.push();
        this.frame.register(method.id, method);
        if (method.args != null)
            method.args.identifiers.forEach(i -> this.frame.register(i, new IntExpression(-1)));
        method.statements.accept(this);
        if (!(this.frame.find(method.ret) instanceof IntExpression)) {
            // error?
        }
        this.frame.pop();
        return null;
    }

    @Override
    public Void visit(Methods methods) {
        methods.methods.forEach(m -> m.accept(this));
        return null;
    }

    @Override
    public Void visit(Program program) {
        this.frame = new ReferenceFrame();
        frame.register(new Identifier("times"), new Method(new Identifier("times"), new Arguments(new Identifier("x"), new Identifier("y")), null, null, null));
        frame.register(new Identifier("plus"), new Method(new Identifier("plus"), new Arguments(new Identifier("x"), new Identifier("y")), null, null, null));
        frame.register(new Identifier("minus"), new Method(new Identifier("minus"), new Arguments(new Identifier("x"), new Identifier("y")), null, null, null));
        frame.register(new Identifier("divide"), new Method(new Identifier("divide"), new Arguments(new Identifier("x"), new Identifier("y")), null, null, null));
        program.methods.accept(this);
        return null;
    }
}
