package func.visitors;

import func.syntax.*;
import func.syntax.bop.Eq;
import func.syntax.bop.Less;
import func.syntax.bop.LessEq;
import func.syntax.bop.NEq;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.*;
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

    T visit(Eq eq);

    T visit(Less less);

    T visit(LessEq lessEq);

    T visit(NEq nEq);

    T visit(Arguments arguments);

    T visit(Condition condition);

    T visit(Identifier identifier);

    T visit(Method method);

    T visit(Methods methods);

    T visit(Program program);
}
