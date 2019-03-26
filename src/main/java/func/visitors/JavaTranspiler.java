package func.visitors;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
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

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Takes the syntax tree and either
 * a) transpiles the program to Java
 * b) compiles the program to JVM bytecode
 */
public class JavaTranspiler implements ASTVisitor<String> {

    private final Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/java", ".java"));
    private final String name;

    public JavaTranspiler(String name) {
        this.name = name;
    }

    public static class JavaSource extends SimpleJavaFileObject {
        private final String code;

        public JavaSource(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    @Override
    public String visit(Assign cmd) {
        return cmd.id.accept(this) + " = " + cmd.expression.accept(this);
    }

    @Override
    public String visit(If cmd) {
        Map<String, String> context = new HashMap<>();
        context.put("cond", cmd.cond.accept(this));
        context.put("then", cmd.then.accept(this));
        context.put("otherwise", cmd.otherwise.accept(this));

        try {
            Template programTemplate = handlebars.compile("if");
            return programTemplate.apply(context);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String visit(While cmd) {
        Map<String, String> context = new HashMap<>();
        context.put("cond", cmd.cond.accept(this));
        context.put("then", cmd.statements.accept(this));

        try {
            Template programTemplate = handlebars.compile("while");
            return programTemplate.apply(context);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String visit(Read read) {
        return read.id.accept(this) + " = read()";
    }

    @Override
    public String visit(Write write) {
        return "write(" + write.exp.accept(this) + ")";
    }

    @Override
    public String visit(Expressions expressions) {
        return expressions.expressions.stream().map(Objects::toString).collect(Collectors.joining(", "));
    }

    @Override
    public String visit(IntExpression intExpression) {
        return intExpression.integer.toString();
    }

    @Override
    public String visit(FunctionExpression functionExpression) {
        String functionExp = functionExpression.id.accept(this);
        if (functionExpression.expressions != null) {
            functionExp += "(";
            functionExp += functionExpression.expressions.accept(this);
            functionExp += ")";
        }
        return functionExp;
    }

    @Override
    public String visit(Statements statements) {
        return statements.statements.stream().map(x -> x.accept(this) + ";").collect(Collectors.joining("\n"));
    }

    @Override
    public String visit(Arguments arguments) {
        return arguments.identifiers.stream().map(x -> "Integer " + x).collect(Collectors.joining(", "));
    }

    @Override
    public String visit(Condition condition) {
        Expression left = condition.exps.expressions.get(0);
        Expression right = condition.exps.expressions.get(1);
        String symbol = null;
        switch (condition.bop) {
            case Eq:
                symbol = "==";
                break;
            case Less:
                symbol = "<";
                break;
            case LessEq:
                symbol = "<=";
                break;
            case NEq:
                symbol = "!=";
                break;
        }
        return left.accept(this) + " " + symbol + " " + right.accept(this);
    }

    @Override
    public String visit(Identifier identifier) {
        return identifier.name;
    }

    @Override
    public String visit(Method method) {
        return this.visit(method, "method");
    }

    public String visit(Method method, String template) {
        Map<String, String> context = new HashMap<>();
        context.put("type", method.args != null ? "Integer" : "void");
        context.put("name", visit(method.id));
        context.put("args", method.args != null ? visit(method.args) : "");
        context.put("vars", method.vars.identifiers.stream().map(x -> "Integer " + x + ";").collect(Collectors.joining("\n")));
        context.put("statements", visit(method.statements));
        context.put("ret", method.ret != null ? "return " + visit(method.ret) + ";" : "");
        try {
            Template programTemplate = handlebars.compile(template);
            return programTemplate.apply(context);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String visit(Methods methods) {
        return methods.methods.stream().map(this::visit).collect(Collectors.joining("\n\n"));
    }

    @Override
    public String visit(Program program) {
        Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/java", ".java"));
        String methods = program.methods.methods.stream()
            .filter(x -> !x.id.name.equals("main"))
            .map(this::visit)
            .collect(Collectors.joining("\n\n"));

        Map<String, String> vars = new HashMap<>();
        vars.put("fileName", name);
        vars.put("methods", methods);

        Method main = program.mainMethod();
        vars.put("main", this.visit(main, "mainMethod"));

        try {
            Template programTemplate = handlebars.compile("program");
            return programTemplate.apply(vars);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
