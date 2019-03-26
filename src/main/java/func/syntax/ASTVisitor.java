package func.syntax;

import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.Assign;
import func.syntax.statement.If;
import func.syntax.statement.Statements;
import func.syntax.statement.While;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;

public interface ASTVisitor<T> {
    T visit(Assign cmd);

    T visit(If cmd);

    T visit(While cmd);

    T visit(Read cmd);

    T visit(Write cmd);

    T visit(Expressions expressions);

    T visit(IntExpression intExpression);

    T visit(FunctionExpression functionExpression);

    T visit(Statements statements);

    T visit(Arguments arguments);

    T visit(Condition condition);

    T visit(Identifier identifier);

    T visit(Method method);

    T visit(Methods methods);

    T visit(Program program);
}
