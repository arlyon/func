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
public class ASTPrinter implements ASTVisitor {

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
    public void visit(Assign assign) {
        indent("");
        visit(assign.id);
        builder.append(" := ");
        visit(assign.expression);
    }

    @Override
    public void visit(If anIf) {
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
    }

    @Override
    public void visit(While aWhile) {
        indent("while ");
        visit(aWhile.cond);
        builder.append("\n");
        indent("begin\n");
        depth += 1;
        visit(aWhile.statements);
        depth -= 1;
        indent("endwhile");
    }

    @Override
    public void visit(Read read) {
        indent("read ");
        visit(read.id);
    }

    @Override
    public void visit(Write write) {
        indent("write ");
        visit(write.exp);
    }

    public void visit(Expression expression) {
        expression.accept(this);
    }

    @Override
    public void visit(Expressions expressions) {
        for (Expression expression : expressions.expressions) {
            visit(expression);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
    }

    @Override
    public void visit(IntExpression intExpression) {
        builder.append(intExpression.integer);
    }

    @Override
    public void visit(FunctionExpression functionExpression) {
        visit(functionExpression.id);
        if (functionExpression.expressions != null) {
            builder.append("(");
            visit(functionExpression.expressions);
            builder.append(")");
        }
    }

    @Override
    public void visit(Statements statements) {
        for (Statement statement : statements.statements) {
            if (statement == null)
                builder.append("<invalid>");
            else
                statement.accept(this);
            this.builder.append(";\n");
        }
    }

    @Override
    public void visit(Eq eq) {
        visit((BinaryOp) eq);
    }

    @Override
    public void visit(Less less) {
        visit((BinaryOp) less);
    }

    @Override
    public void visit(LessEq lessEq) {
        visit((BinaryOp) lessEq);
    }

    @Override
    public void visit(NEq nEq) {
        visit((BinaryOp) nEq);
    }

    public void visit(BinaryOp eq) {
        String name = eq.getClass().getSimpleName();
        builder.append(name.substring(0, 1).toLowerCase()).append(name.substring(1));
    }

    @Override
    public void visit(Arguments arguments) {
        for (Identifier identifier : arguments.identifiers) {
            visit(identifier);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
    }

    @Override
    public void visit(Condition condition) {
        visit(condition.bop);
        builder.append("(");
        visit(condition.exps);
        builder.append(")");
    }

    @Override
    public void visit(Identifier identifier) {
        builder.append(identifier.name);
    }

    @Override
    public void visit(Method method) {
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
    }

    @Override
    public void visit(Methods methods) {
        for (Method method : methods.methods) {
            visit(method);
            builder.append(";\n\n");
        }
        builder.delete(builder.length() - 1, builder.length());
    }

    @Override
    public void visit(Program program) {
        this.visit(program.methods);
    }
}
