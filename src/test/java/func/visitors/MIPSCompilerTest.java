package func.visitors;

import func.syntax.*;
import func.syntax.exp.Expressions;
import func.syntax.exp.FunctionExpression;
import func.syntax.exp.IntExpression;
import func.syntax.statement.*;
import func.syntax.statement.rw.Read;
import func.syntax.statement.rw.Write;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MIPSCompilerTest {

    @Test
    void testThis() {
        assertEquals("$t8", MIPSCompiler.Registers.fromNumber(8));
    }

    @Test
    void testThat() {
        assertEquals(9, MIPSCompiler.Registers.get(MIPSCompiler.Registers.EVAL[1]));
    }

    @Test
    void testFinal() {
        assertEquals("$t10", MIPSCompiler.Registers.fromNumber(26));
    }

    @Test
    void testAssign() {
        List<Method> methods = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        List<Identifier> identifiers = new ArrayList<>();
        identifiers.add(new Identifier("x"));
        identifiers.add(new Identifier("y"));
        statements.add(new Assign(identifiers.get(1), new IntExpression(1)));
        statements.add(new Assign(identifiers.get(0), new IntExpression(0)));
        methods.add(new Method(
            new Identifier("main"),
            null,
            new Arguments(identifiers),
            new Statements(statements),
            null
        ));
        Program p = new Program(new Methods(methods));

        MIPSCompiler comp = new MIPSCompiler();
        comp.visit(p);

        ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner(classLoader.getResourceAsStream("assign.asm"));
        String expected = scanner.useDelimiter("\\A").next();
        assertEquals(expected, comp.toString());
    }

    @Test
    void testReassign() {
        List<Method> methods = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        List<Identifier> identifiers = new ArrayList<>();
        identifiers.add(new Identifier("x"));
        identifiers.add(new Identifier("y"));
        statements.add(new Assign(identifiers.get(1), new IntExpression(1)));
        statements.add(new Assign(identifiers.get(0), new FunctionExpression(identifiers.get(1), null)));
        methods.add(new Method(
            new Identifier("main"),
            null,
            new Arguments(identifiers),
            new Statements(statements),
            null
        ));
        Program p = new Program(new Methods(methods));

        MIPSCompiler comp = new MIPSCompiler();
        comp.visit(p);

        ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner(classLoader.getResourceAsStream("reassign.asm"));
        String expected = scanner.useDelimiter("\\A").next();
        assertEquals(expected, comp.toString());
    }

    @Test
    void testRead() {
        List<Method> methods = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        List<Identifier> identifiers = new ArrayList<>();
        identifiers.add(new Identifier("x"));
        statements.add(new Read(identifiers.get(0)));
        methods.add(new Method(
            new Identifier("main"),
            null,
            new Arguments(identifiers),
            new Statements(statements),
            null
        ));
        Program p = new Program(new Methods(methods));

        MIPSCompiler comp = new MIPSCompiler();
        comp.visit(p);

        ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner(classLoader.getResourceAsStream("read.asm"));
        String expected = scanner.useDelimiter("\\A").next();
        assertEquals(expected, comp.toString());
    }

    @Test
    void testWrite() {
        List<Method> methods = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        List<Identifier> identifiers = new ArrayList<>();
        statements.add(new Write(new IntExpression(1)));
        methods.add(new Method(
            new Identifier("main"),
            null,
            new Arguments(identifiers),
            new Statements(statements),
            null
        ));
        Program p = new Program(new Methods(methods));

        MIPSCompiler comp = new MIPSCompiler();
        comp.visit(p);

        ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner(classLoader.getResourceAsStream("write.asm"));
        String expected = scanner.useDelimiter("\\A").next();
        assertEquals(expected, comp.toString());
    }

    @Test
    void testIf() {
        List<Method> methods = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        List<Identifier> identifiers = new ArrayList<>();

        identifiers.add(new Identifier("x"));
        identifiers.add(new Identifier("y"));
        statements.add(new Assign(identifiers.get(0), new IntExpression(0)));
        statements.add(new Assign(identifiers.get(1), new IntExpression(1)));
        statements.add(new If(
            new Condition(BinaryOp.Eq, new Expressions(
                new FunctionExpression(identifiers.get(0), null),
                new FunctionExpression(identifiers.get(1), null)
            )),
            new Statements(new Assign(identifiers.get(0), new IntExpression(100))),
            new Statements(new Assign(identifiers.get(0), new FunctionExpression(identifiers.get(1), null)))
        ));

        methods.add(new Method(
            new Identifier("main"),
            null,
            new Arguments(identifiers),
            new Statements(statements),
            null
        ));

        Program p = new Program(new Methods(methods));
        MIPSCompiler comp = new MIPSCompiler();
        comp.visit(p);

        ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner(classLoader.getResourceAsStream("if.asm"));
        String expected = scanner.useDelimiter("\\A").next();
        assertEquals(expected, comp.toString());
    }

    @Test
    void testWhile() {
        List<Method> methods = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        List<Identifier> identifiers = new ArrayList<>();

        identifiers.add(new Identifier("x"));
        identifiers.add(new Identifier("y"));
        statements.add(new Assign(identifiers.get(0), new IntExpression(0)));
        statements.add(new Assign(identifiers.get(1), new IntExpression(100)));
        statements.add(new While(
            new Condition(BinaryOp.Eq, new Expressions(
                new FunctionExpression(identifiers.get(0), null),
                new FunctionExpression(identifiers.get(1), null)
            )),
            new Statements(new Assign(identifiers.get(0), new IntExpression(100)))
        ));

        methods.add(new Method(
            new Identifier("main"),
            null,
            new Arguments(identifiers),
            new Statements(statements),
            null
        ));

        Program p = new Program(new Methods(methods));
        MIPSCompiler comp = new MIPSCompiler();
        comp.visit(p);

        ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner(classLoader.getResourceAsStream("while.asm"));
        String expected = scanner.useDelimiter("\\A").next();
        assertEquals(expected, comp.toString());
    }

    @Test
    void testBuiltin() {
        List<Method> methods = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        List<Identifier> identifiers = new ArrayList<>();

        identifiers.add(new Identifier("x"));
        statements.add(new Assign(identifiers.get(0), new IntExpression(1)));
        statements.add(new Assign(identifiers.get(0), new FunctionExpression(new Identifier("plus"),
            new Expressions(
                new FunctionExpression(identifiers.get(0), null),
                new IntExpression(10)
            )
        )));

        methods.add(new Method(
            new Identifier("main"),
            null,
            new Arguments(identifiers),
            new Statements(statements),
            null
        ));

        Program p = new Program(new Methods(methods));
        MIPSCompiler comp = new MIPSCompiler();
        comp.visit(p);

        ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner(classLoader.getResourceAsStream("builtin.asm"));
        String expected = scanner.useDelimiter("\\A").next();
        assertEquals(expected, comp.toString());
    }
}