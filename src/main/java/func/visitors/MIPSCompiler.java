package func.visitors;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import func.Frame;
import func.errors.SemanticError;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static func.Func.builtins;

public class MIPSCompiler implements ASTVisitor<Void> {

    private List<SemanticError> errors = new LinkedList<>();

    static class SysCalls {
        static final int PRINT_INT = 1; // a0 = integer to print
        static final int PRINT_STRING = 4; // a0 = address of null-terminated string to print
        static final int READ_INT = 5; // v0 contains integer read
        static final int EXIT = 10;
    }

    static class Registers {
        static String[] ZERO = {"$zero"};
        static String[] PSEUDO = {"$at"};
        static String[] RETURN = generateRegisters("$v", 0, 1);
        static String[] ARGUMENTS = generateRegisters("$a", 0, 3);
        static String[] EVAL = generateRegisters("$t", 8, 9);
        static String[] CALLEE = generateRegisters("$s", 0, 7);
        static String[] TEMP = generateRegisters("$t", 0, 7);

        private static String[][] REGISTERS = {ZERO, PSEUDO, RETURN, ARGUMENTS, EVAL, CALLEE, TEMP};

        public static String[] registers() {
            return Arrays.stream(REGISTERS).flatMap(Arrays::stream).toArray(String[]::new);
        }

        public static String fromNumber(int regNumber) {
            String[] registers = registers();
            return Arrays.stream(registers).skip(regNumber).findFirst().orElse("$t" + (regNumber - registers.length + 10));
        }

        public static int get(String registerName) {
            int register = 0;
            for (String s : registers()) {
                if (s.equals(registerName)) return register;
                register++;
            }
            return -1;
        }

        private static String[] generateRegisters(String ident, int min, int max) {
            int registerCount = max - min + 1;
            String[] registers = new String[registerCount];
            for (Integer i = 0; i < registerCount; i++) {
                registers[i] = ident + (min + i);
            }
            return registers;
        }
    }

    private SemanticError error(String message, AST... nodes) {
        SemanticError e = new SemanticError(message, nodes);
        this.errors.add(e);
        return e;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    private int registerDestination;

    private int labelCounter;
    private Frame frame;
    private StringBuilder builder;

    public MIPSCompiler() {
        this.frame = new Frame(Registers.get(Registers.CALLEE[0]));
        this.labelCounter = 0;
        this.builder = new StringBuilder();
    }

    private void pushRegister(int r) {
        pushRegister(Registers.fromNumber(r));
    }

    /**
     * Pushes the value of a given register to the stack.
     */
    private void pushRegister(String reg) {
        builder.append("\taddi $sp, $sp, -4\n"); // decrement the stack pointer by a word
        builder.append(String.format("\tsw %s, 0($sp)\n", reg)); // store it into the register
    }

    private void popRegister(int r) {
        popRegister(Registers.fromNumber(r));
    }

    /**
     * Pops a value off the stack (into the given register)
     */
    private void popRegister(String reg) {
        builder.append(String.format("\tlw %s, 0($sp)\n", reg)); // load the stack pointer
        builder.append("\taddi $sp, $sp, 4\n"); // increment it by a word
    }

    @Override
    public Void visit(Assign cmd) {
        if (!frame.exists(cmd.id)) error("Assigned variable not present in stack", cmd.id);
        registerDestination = frame.find(cmd.id);
        cmd.expression.accept(this);
        return null;
    }

    @Override
    public Void visit(If cmd) {
        int labelNumber = this.labelCounter++;

        builder.append("\t# if statement: ").append(cmd).append("\n");
        this.visit(cmd.cond);
        builder.append(", ").append("ift").append(labelNumber).append("\n");
        if (cmd.otherwise != null) {
            this.visit(cmd.otherwise);
        }
        builder.append("\tj ife").append(labelNumber).append("\n");
        builder.append("ift").append(labelNumber).append(":\n");
        this.visit(cmd.then);
        builder.append("ife").append(labelNumber).append(":\n");
        return null;
    }

    @Override
    public Void visit(While cmd) {
        int labelNumber = this.labelCounter++;

        builder.append("\t# while loop: ").append(cmd).append("\n");
        builder.append("wls").append(labelNumber).append(":\n");
        this.visit(cmd.cond);
        builder.append(", ").append("wle").append(labelNumber).append("\n");
        this.visit(cmd.statements);
        builder.append("\tj wls").append(labelNumber).append("\n");
        builder.append("wle").append(labelNumber).append(":\n");
        return null;
    }

    @Override
    public Void visit(Read cmd) {
        if (!frame.exists(cmd.id))
            error("Variable to be read does not exist.");

        // print the input prompt
        builder.append("\tla $a0, sinp\n");
        builder.append("\tli $v0, ").append(SysCalls.PRINT_STRING).append("\n");
        builder.append("\tsyscall\n");
        // read the input
        builder.append("\tli $v0, ").append(SysCalls.READ_INT).append("\n");
        builder.append("\tsyscall\n");
        builder.append("\tmove ").append(Registers.fromNumber(frame.find(cmd.id))).append(",").append("$v0").append("\n");
        return null;
    }

    @Override
    public Void visit(Write cmd) {
        registerDestination = Registers.get(Registers.ARGUMENTS[0]);
        cmd.exp.accept(this);
        builder.append("\tli $v0, " + SysCalls.PRINT_INT + "\n");
        builder.append("\tsyscall\n");
        return null;
    }

    @Override
    public Void visit(Expressions expressions) {
        throw new NotImplementedException(); // handled by function expression
    }

    /**
     * Move a constant into registerDestination
     */
    @Override
    public Void visit(IntExpression intExpression) {
        builder.append("\tli ").append(Registers.fromNumber(registerDestination)).append(", ").append(intExpression.integer).append("\n");
        return null;
    }

    /**
     * Either
     * - move a variable into registerDestination
     * - call a function and move it
     */
    @Override
    public Void visit(FunctionExpression functionExpression) {
        if (functionExpression.expressions != null) {
            if (builtins.contains(functionExpression.id)) handleBuiltin(functionExpression);
            else handleFunction(functionExpression);
        } else {
            builder.append("\tmove ").append(Registers.fromNumber(registerDestination)).append(", ").append(Registers.fromNumber(frame.find(functionExpression.id))).append("\n");
        }
        return null;
    }

    private void handleFunction(FunctionExpression functionExpression) {
        builder.append("\t# function call: ").append(functionExpression).append("\n");
        int destination = registerDestination;
        for (int i = 0; i < functionExpression.expressions.expressions.size(); i++) {
            Expression e = functionExpression.expressions.expressions.get(i);
            registerDestination = Registers.get(Registers.ARGUMENTS[i]);
            e.accept(this);
        }
        registerDestination = destination;
        builder.append("\tjal ").append(functionExpression.id).append("\n");
        builder.append("\tmove ").append(Registers.fromNumber(registerDestination)).append(", ").append(Registers.RETURN[0]).append("\n");
    }

    private void handleBuiltin(FunctionExpression functionExpression) {
        String command = null;
        int originalDest = registerDestination;

        String builtinName = functionExpression.id.name;
        switch (builtinName) {
            case "plus":
                command = "add";
                break;
            case "times":
                command = "mult";
                break;
            case "divide":
                command = "div";
                break;
            case "minus":
                command = "sub";
                break;
        }

        // we know from type checking that there are always 2 arguments
        registerDestination = Registers.get(Registers.EVAL[0]);
        functionExpression.expressions.expressions.get(0).accept(this);
        registerDestination = Registers.get(Registers.EVAL[1]);
        functionExpression.expressions.expressions.get(1).accept(this);
        builder.append("\t").append(command)
            .append(" ").append(Registers.fromNumber(originalDest))
            .append(", ").append(Registers.EVAL[0])
            .append(", ").append(Registers.EVAL[1])
            .append("\n");
    }

    @Override
    public Void visit(Statements statements) {
        statements.statements.forEach(x -> x.accept(this));
        return null;
    }

    @Override
    public Void visit(Arguments arguments) {
        return null;
    }

    @Override
    public Void visit(Condition condition) {
        String instruction = null;
        switch (condition.bop) {
            case Less:
                instruction = "blt";
                break;
            case LessEq:
                instruction = "ble";
                break;
            case Eq:
                instruction = "beq";
                break;
            case NEq:
                instruction = "bne";
                break;
        }
        // todo store register
        registerDestination = Registers.get(Registers.EVAL[0]);
        condition.exps.expressions.get(0).accept(this);
        pushRegister(Registers.get(Registers.EVAL[0]));
        registerDestination = Registers.get(Registers.EVAL[1]);
        condition.exps.expressions.get(1).accept(this);
        popRegister(Registers.get(Registers.EVAL[0]));

        builder.append("\t").append(instruction).append(" ").append(Registers.EVAL[0]).append(", ").append(Registers.EVAL[1]);
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        return null;
    }

    @Override
    public Void visit(Method method) {
        builder.append(method.id.name).append(":\n");
        int registers = method.variables().size();

        // create a new frame and add the registers
        frame = frame.push();
        method.variables().forEach(frame::register);

        // store previous s-registers
        if (!method.id.name.equals("main")) {
            builder.append("\t# function "+method.id.name+" load\n");
            pushRegister("$ra");
            int argCount = method.args != null ? method.args.identifiers.size() : 0;
            for (int i = 0; i < registers; i++) {
                int dest = Registers.get(Registers.CALLEE[i]);
                pushRegister(dest);
                if (i < argCount) {
                    builder.append("\tmove ").append(Registers.fromNumber(dest)).append(", ").append(Registers.ARGUMENTS[i]).append("\n");
                }
            }
        }

        builder.append("\t# function "+method.id.name+" begin\n");
        this.visit(method.statements);

        if (method.ret != null) {
            int register = frame.find(method.ret);
            builder.append("\tmove " + Registers.RETURN[0] + ", " + Registers.fromNumber(register) + "\n");
        }

        // load previous s-registers
        if (!method.id.name.equals("main")) {
            builder.append("\t# function " + method.id.name + " unload\n");
            for (int i = registers - 1; i >= 0; i--) {
                popRegister(Registers.get(Registers.CALLEE[i]));
            }
            popRegister("$ra");
        }

        if (method.id.name.equals("main")) {
            builder.append("\tli $v0, " + SysCalls.EXIT + "\n");
            builder.append("\tsyscall\n");
        } else {
            builder.append("\tjr $ra\n");
        }
        frame = frame.pop();
        return null;
    }

    @Override
    public Void visit(Methods methods) {
        methods.methods.forEach(this::visit);
        return null;
    }

    @Override
    public Void visit(Program program) {
        program.methods.accept(this);
        Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/asm", ".asm"));
        try {
            Template programTemplate = handlebars.compile("program");
            String mipsCode = programTemplate.apply(builder.toString());
            builder = new StringBuilder(mipsCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
