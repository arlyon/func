package func.syntax.statement;

import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.ReadWrite;
import func.syntax.statement.rw.Write;

public interface StatementVisitor {
    void visit(Assign cmd);
    void visit(If cmd);
    void visit(While cmd);
    void visit(Read cmd);
    void visit(Write write);
}
