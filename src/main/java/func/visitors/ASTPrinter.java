package func.visitors;

import func.syntax.*;
import func.syntax.bop.*;
import func.syntax.exp.Expression;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.*;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class ASTPrinter implements ASTVisitor<Void> {

    final int indentation = 4;
    private int depth = 0;
    private StringBuilder builder = new StringBuilder();

    public String toString() {
        return builder.toString();
    }

    private StringBuilder indent(Object toAdd) {
        this.builder.append(new String(new char[this.indentation * this.depth]).replace("\0", " "));
        this.builder.append(toAdd);
        return this.builder;
    }

    @Override
    public Void visit(Assign assign) {
        indent("");
        visit(assign.id);
        builder.append(" := ");
        visit(assign.expression);
        return null;
    }

    @Override
    public Void visit(If anIf) {
        indent("if ").append(anIf.cond).append("\n");
        indent("then\n");
        depth += 1;
        visit(anIf.then);
        depth -= 1;
        if (anIf.otherwise != null) {
            indent("else\n");
            depth += 1;
            visit(anIf.otherwise);
            depth -= 1;
        }
        indent("endif");
        return null;
    }

    @Override
    public Void visit(While aWhile) {
        indent("while ");
        visit(aWhile.cond);
        builder.append("\n");
        indent("begin\n");
        depth += 1;
        visit(aWhile.statements);
        depth -= 1;
        indent("endwhile");
        return null;
    }

    @Override
    public Void visit(Read read) {
        indent("read ");
        visit(read.id);
        return null;
    }

    @Override
    public Void visit(Write write) {
        indent("write ");
        visit(write.exp);
        return null;
    }

    public Void visit(Expression expression) {
        expression.accept(this);
        return null;
    }

    @Override
    public Void visit(Expressions expressions) {
        for (Expression expression : expressions.expressions) {
            visit(expression);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return null;
    }

    @Override
    public Void visit(IntExpression intExpression) {
        builder.append(intExpression.integer);
        return null;
    }

    @Override
    public Void visit(FunctionExpression functionExpression) {
        visit(functionExpression.id);
        if (functionExpression.expressions != null) {
            builder.append("(");
            visit(functionExpression.expressions);
            builder.append(")");
        }
        return null;
    }

    @Override
    public Void visit(Statements statements) {
        for (Statement statement : statements.statements) {
            if (statement == null)
                builder.append("<invalid>");
            else
                statement.accept(this);
            this.builder.append(";\n");
        }
        return null;
    }

    @Override
    public Void visit(Eq eq) {
        visit((BinaryOp) eq);
        return null;
    }

    @Override
    public Void visit(Less less) {
        visit((BinaryOp) less);
        return null;
    }

    @Override
    public Void visit(LessEq lessEq) {
        visit((BinaryOp) lessEq);
        return null;
    }

    @Override
    public Void visit(NEq nEq) {
        visit((BinaryOp) nEq);
        return null;
    }

    public Void visit(BinaryOp eq) {
        String name = eq.getClass().getSimpleName();
        builder.append(name.substring(0, 1).toLowerCase()).append(name.substring(1));
        return null;
    }

    @Override
    public Void visit(Arguments arguments) {
        for (Identifier identifier : arguments.identifiers) {
            visit(identifier);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return null;
    }

    @Override
    public Void visit(Condition condition) {
        visit(condition.bop);
        builder.append("(");
        visit(condition.exps);
        builder.append(")");
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        builder.append(identifier.name);
        return null;
    }

    @Override
    public Void visit(Method method) {
        builder.append("method ");
        this.visit(method.id);
        builder.append("(");
        if (method.args != null) this.visit(method.args);
        builder.append(")");
        if (method.vars != null) {
            builder.append(" vars ");
            this.visit(method.vars);
        }
        builder.append("\n");
        builder.append("begin\n");
        this.depth += 1;
        this.visit(method.statements);
        if (method.ret != null) {
            indent("return ");
            this.visit(method.ret);
            builder.append(";\n");
        }
        this.depth -= 1;
        builder.append("endmethod");
        return null;
    }

    @Override
    public Void visit(Methods methods) {
        for (Method method : methods.methods) {
            visit(method);
            builder.append(";\n\n");
        }
        builder.delete(builder.length() - 1, builder.length());
        return null;
    }

    @Override
    public Void visit(Program program) {
        this.visit(program.methods);
        return null;
    }
}
