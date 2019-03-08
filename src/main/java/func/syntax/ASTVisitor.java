package func.syntax;

import func.syntax.bop.Eq;
import func.syntax.bop.Less;
import func.syntax.bop.LessEq;
import func.syntax.bop.NEq;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.Assign;
import func.syntax.statement.If;
import func.syntax.statement.Statements;
import func.syntax.statement.While;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;

public interface ASTVisitor {
    void visit(Assign cmd);

    void visit(If cmd);

    void visit(While cmd);

    void visit(Read cmd);

    void visit(Write cmd);

    void visit(Expressions expressions);

    void visit(IntExpression intExpression);

    void visit(FunctionExpression functionExpression);

    void visit(Statements statements);

    void visit(Eq eq);

    void visit(Less less);

    void visit(LessEq lessEq);

    void visit(NEq nEq);

    void visit(Arguments arguments);

    void visit(Condition condition);

    void visit(Identifier identifier);

    void visit(Method method);

    void visit(Methods methods);

    void visit(Program program);
}
