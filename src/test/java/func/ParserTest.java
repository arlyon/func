package func;

import func.syntax.Arguments;
import func.syntax.Condition;
import func.syntax.Method;
import func.syntax.bop.Eq;
import func.syntax.statement.Assign;
import func.syntax.statement.If;
import func.syntax.statement.Statements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

public class ParserTest {

    @Test
    public void testArguments() {
        String example = "a, b, c, d";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Arguments a = p.arguments();
        Assertions.assertEquals(0, p.getErrors().size());
        Assertions.assertEquals(a.toString(), example);
    }

    @Test
    public void testVars() {
        String example = "vars a, b begin";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Arguments a = p.vars();
        Assertions.assertEquals(0, p.getErrors().size());
        Assertions.assertEquals("vars " + a.toString() + " begin", example);
    }

    /**
     * Vars with no arguments should gracefully fail.
     */
    @Test
    public void testVarsEmpty() {
        String example = "vars begin";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Arguments a = p.vars();
        Assertions.assertEquals(1, p.getErrors().size());
        Assertions.assertNull(a);
    }

    // Vars with missing "vars" should throw match error.
    @Test
    public void testVarsNoVar() {
        String example = "a, b, c";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Assertions.assertThrows(MatchError.class, p::vars);
    }

    // trivial sanity case
    @Test
    public void testMethod() {
        String example = "method main() begin x := x; endmethod;";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Method m = p.method();
        Assertions.assertEquals(0, p.getErrors().size());
        Assertions.assertEquals("main", m.id.name);
        Assertions.assertNull(m.args);
        Assertions.assertNull(m.vars);
        Assertions.assertNull(m.ret);
        Assertions.assertEquals(1, m.statements.statements.size());
    }

    @Test
    public void testMethodWithArgs() {
        String example = "method main(x,y,z) begin x := x; endmethod;";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Method m = p.method();
        Assertions.assertEquals(0, p.getErrors().size());
        Assertions.assertEquals(3, m.args.identifiers.size());
    }

    @Test
    public void testMethodWithVars() {
        String example = "method main() vars x, y, z begin x := x; endmethod;";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Method m = p.method();
        Assertions.assertEquals(0, p.getErrors().size());
        Assertions.assertEquals(3, m.vars.identifiers.size());
    }

    @Test
    public void testMethodWithReturn() {
        String example = "method main() begin x := x; return x; endmethod;";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Method m = p.method();
        Assertions.assertEquals(0, p.getErrors().size());
        Assertions.assertEquals("x", m.ret.name);
    }

    @Test
    public void testMethodBroken() {
        String example = "method main) x := x; endmethod;";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Method m = p.method();
        Assertions.assertEquals("main", m.id.name);
        Assertions.assertEquals("x := x;\n", m.statements.toString());
    }

    /**
     * Assert that a badly formatted method can still be parsed.
     */
    @Test
    public void testMethodBrokenElements() {
        String example = "method 123(x y z) vars 1 2 begin !@£!@!D; endmethod;";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Method m = p.method();
        Assertions.assertNull(m.id);
        Assertions.assertEquals(1, m.args.identifiers.size());
        Assertions.assertNull(m.vars);
        Assertions.assertEquals(1, m.statements.statements.size());
        Assertions.assertNull(m.statements.statements.get(0));
        Assertions.assertNull(m.ret);
    }

    @Test
    public void testMethodBrokenElements2() {
        String example = "method 123x y z) vars 1 2 begin if eq(1,1) then 1 endif; endmethod;";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Method m = p.method();
        Assertions.assertEquals(5, p.getErrors().size());
        Assertions.assertNull(m.id);
        Assertions.assertNull(m.args);
        Assertions.assertNull(m.vars);
        Assertions.assertEquals("if eq(1, 1)\nthen\n<invalid>;\nendif;\n", m.statements.toString());
    }

    @Test
    public void testStatementAssign() {
        String example = "x := 1";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Assign a = (Assign) p.statement();
        Assertions.assertEquals(0, p.getErrors().size());
        Assertions.assertEquals("x := 1", a.toString());
    }

    @Test
    public void testStatementsNoSemi() {
        String example = "x := 1 endmethod";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Statements a = p.statements();
        Assertions.assertEquals(1, p.getErrors().size());
    }


    @Test
    public void testStatementInvalid() {
        String example = "12 ?";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Assertions.assertThrows(MatchError.class, p::statement);
    }

    @Test
    public void testStatementsInvalid() {
        String example = "a := b; 12 ?; read x;";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Statements s = p.statements();
        Assertions.assertEquals(1, p.getErrors().size());
        Assertions.assertEquals(3, s.statements.size());
    }

    @Test
    public void testStatementsIf() {
        String example = "if eq(1,1) then 1 endif";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        If s = (If) p.statement();
        Assertions.assertEquals(1, p.getErrors().size());
        Assertions.assertEquals(null, s.otherwise);
        Assertions.assertEquals(1, s.then.statements.size());
    }

    @Test
    public void testCondInvalid() {
        String example = "eq(1 1)";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Condition b = p.cond();
        Assertions.assertEquals(1, p.getErrors().size());
        Assertions.assertEquals(Eq.class, b.bop.getClass());
        Assertions.assertEquals(1, b.exps.expressions.size());
    }


    @Test
    public void testCondInvalid2() {
        String example = "eq1 1)";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Condition b = p.cond();
        Assertions.assertEquals(3, p.getErrors().size());
        Assertions.assertNull(b.bop);
        Assertions.assertEquals(1, b.exps.expressions.size());
    }

    @Test
    public void testCond() {
        String example = "eq(1, 1)";
        Lexer l = new Lexer(new StringReader(example));
        Parser p = new Parser(l);

        Condition b = p.cond();
        Assertions.assertEquals(0, p.getErrors().size());
    }
}
