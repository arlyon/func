package func;

import func.syntax.Identifier;
import func.syntax.Program;
import func.visitors.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * The command line interface to the Functools program.
 * Contains two commands, format and validate.
 */
@Command(name = "func", mixinStandardHelpOptions = true, version = "functools version 2.1.1")
public class Func implements Callable<Void> {

    public static final AbstractList<Identifier> builtins = new ArrayList<>(Arrays.asList(
        new Identifier("plus"), new Identifier("minus"),
        new Identifier("times"), new Identifier("divide")
    ));

    public static void main(String[] args) {
        CommandLine.call(new Func(), args);
    }

    @Override
    public Void call() {
        CommandLine.usage(this.getClass(), System.out);
        return null;
    }

    @Command(
        name = "format",
        description = "Parses and formats a func file. " +
            "If infile and outfile are omitted, format reformats stdin to stdout.",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n"
    )
    void format(
        @Parameters(paramLabel = "<infile>", description = "read input from file") File inFile,
        @Option(names = "-o", paramLabel = "<outfile>", description = "direct output to file") String outFile
    ) {
        InputStream in = getInputStream(inFile);
        Program program = parseProgram(in);
        writeStringToOutput(outFile, program.toString());
    }

    @Command(
        name = "validate",
        description = "Parses and validates a func file. " +
            "Returns exit code 1 if the program is correctly formatted, else a 0.",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n"
    )
    void validate(
        @Parameters(paramLabel = "<infile>", description = "read input from file") File inFile
    ) throws NoSuchAlgorithmException {
        InputStream in = getInputStream(inFile);
        DigestInputStream digestStream = new DigestInputStream(in, MessageDigest.getInstance("SHA-256"));
        Program program = parseProgram(digestStream);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (byte aByte : program.toString().getBytes()) md.update(aByte);

        byte[] dig = digestStream.getMessageDigest().digest();
        byte[] dig2 = md.digest();

        System.out.println("source:    " + String.format("%0" + (dig.length << 1) + "x", new BigInteger(1, dig)));
        System.out.println("formatted: " + String.format("%0" + (dig2.length << 1) + "x", new BigInteger(1, dig2)));
        System.exit(Arrays.equals(dig, dig2) ? 0 : 1);
    }

    @Command(
        name = "compile",
        description = "Compiles the provided source code into a lower level representation.",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n"
    )
    void compile(
        @Parameters(paramLabel = "<infile>", description = "read input from file") File inFile,
        @Option(names = "-o", paramLabel = "<outfile>", description = "direct output to file. if exporting to JAVA or JVM, simply supply the class name.") String outFile,
        @Option(names = "-t", paramLabel = "<format>", description = "the format of the output: ${COMPLETION-CANDIDATES} (defaults to MIPS)", defaultValue = "MIPS") OutputFormat outputFormat
    ) {
        InputStream in = getInputStream(inFile);
        Program program = parseProgram(in);
        JavaTranspiler jc;

        String outputCode = null;
        switch (outputFormat) {
            case MIPS:
                MIPSCompiler mc = new MIPSCompiler();
                mc.visit(program);
                outputCode = mc.toString();
                break;
            case JAVA:
                String className = outFile == null ? "Program" : outFile.split("\\.")[0];
                jc = new JavaTranspiler(className);
                outputCode = jc.visit(program);
                break;
            case JVM:
                if (outFile == null) {
                    System.err.println("Could not compile for JVM: JVM compile requires an outfile that is a valid java identifier (ie. \"Program\")\n");
                    CommandLine.usage(this, System.err);
                    System.exit(1);
                }

                if (!outFile.chars().allMatch(c -> Character.isJavaIdentifierPart((char) c))) {
                    System.err.println("Could not compile for JVM: \""+outFile+"\" is not a valid Java identifier.");
                    System.exit(1);
                }

                jc = new JavaTranspiler(outFile);
                compileJVM(jc.visit(program), outFile);
                break;
        }

        writeStringToOutput(outFile, outputCode);
    }

    private void compileJVM(String sourceCode, String className) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaTranspiler.JavaSource source = new JavaTranspiler.JavaSource(className, sourceCode);
        Iterable<? extends JavaFileObject> fileObjects = Collections.singletonList(source);
        StringWriter output = new StringWriter();
        boolean succ = compiler.getTask(output, null, null, null, null, fileObjects).call();
        if (!succ) {
            System.err.println(output.toString());
            System.exit(1);
        } else {
            System.out.println("Compiled to "+className+".class");
            System.exit(0);
        }
    }

    private void writeStringToOutput(String outFile, String outputCode) {
        try {
            Writer out = outFile != null ? new FileWriter(outFile) : new OutputStreamWriter(System.out);
            out.write(outputCode);
            out.close();
        } catch (IOException e) {
            System.err.println("Could not write to file: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    public static Program parseProgram(InputStream stream) {
        Lexer symbols = new Lexer(new InputStreamReader(stream));
        Parser p = new Parser(symbols);
        boolean errors = false;
        Program program = p.program();

        if (p.hasErrors()) {
            errors = true;
            System.err.println("There are syntax errors with your program:");
            System.err.println(p.getErrors().stream().map(err -> "\t" + err).collect(Collectors.joining("\n")));
        }

        SemanticAnalyser sa = new SemanticAnalyser();
        if (!program.accept(sa)) {
            errors = true;
            System.err.println("There are semantic errors with your program:");
            System.err.println(sa.getErrors().stream().map(err -> "\t" + err).collect(Collectors.joining("\n")));
        }

        AssignmentAnalyser aa = new AssignmentAnalyser();
        if (!program.accept(aa)) {
            errors = true;
            System.err.println("There are assignment errors with your program:");
            System.err.println(aa.getErrors().stream().map(err -> "\t" + err).collect(Collectors.joining("\n")));
        }

        TypeChecker fv = new TypeChecker();
        program.accept(fv);
        if (fv.getErrors().size() > 0) {
            errors = true;
            System.err.println("There are type errors with your program:");
            System.err.println(fv.getErrors().stream().map(err -> "\t" + err).collect(Collectors.joining("\n")));
        }

        CleanTree ct = new CleanTree();
        program = ct.visit(program);

        if (errors) {
            System.exit(1);
        }

        return program;
    }

    private InputStream getInputStream(File inFile) {
        InputStream in = null;
        try {
            in = inFile != null ? new FileInputStream(inFile) : System.in;
        } catch (FileNotFoundException e) {
            System.err.println("Error with your file: " + e.getLocalizedMessage());
            System.exit(1);
        }
        return in;
    }

    private enum OutputFormat {
        JVM, JAVA, MIPS
    }
}
