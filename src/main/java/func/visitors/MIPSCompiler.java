package func.visitors;

import func.syntax.*;
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

public class MIPSCompiler implements ASTVisitor<Void> {

    @Override
    public String toString() {
        return "";
    }

    @Override
    public Void visit(Assign cmd) {
        return null;
    }

    @Override
    public Void visit(If cmd) {
        return null;
    }

    @Override
    public Void visit(While cmd) {
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
        return null;
    }

    @Override
    public Void visit(Statements statements) {
        return null;
    }

    @Override
    public Void visit(Eq eq) {
        return null;
    }

    @Override
    public Void visit(Less less) {
        return null;
    }

    @Override
    public Void visit(LessEq lessEq) {
        return null;
    }

    @Override
    public Void visit(NEq nEq) {
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
        return null;
    }

    @Override
    public Void visit(Methods methods) {
        return null;
    }

    @Override
    public Void visit(Program program) {
        return null;
    }
}
