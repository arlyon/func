package func;

import func.syntax.Program;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Help.ColorScheme;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Command(name = "func", mixinStandardHelpOptions = true, version = "functools version 1.0.0")
public class Func implements Callable<Void> {
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
        @Option(names = "-o", paramLabel = "<outfile>", description = "direct output to file") File outFile
    ) {
        InputStream in = null;
        try {
            in = inFile != null ? new FileInputStream(inFile) : System.in;
        } catch (FileNotFoundException e) {
            System.err.println("Error with your file: " + e.getLocalizedMessage());
            System.exit(1);
        }

        Lexer symbols = new Lexer(new InputStreamReader(in));
        Parser p = new Parser(symbols);
        Program program = null;
        try {
            program = p.program();
        } catch (BadSyntax e) {
            System.err.println("Could not parse program! " + e);
            System.exit(1);
        }

        try {
            Writer out = outFile != null ? new FileWriter(outFile) : new OutputStreamWriter(System.out);
            out.write(program.toString());
            out.close();
        } catch (IOException e) {
            System.err.println("Could not write to file: " + e.getLocalizedMessage());
            System.exit(1);
        }
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

        InputStream in = null;
        try {
            in = inFile != null ? new FileInputStream(inFile) : System.in;
        } catch (FileNotFoundException e) {
            System.err.println("Error with your file: " + e.getLocalizedMessage());
            System.exit(1);
        }

        DigestInputStream digestStream = new DigestInputStream(in, MessageDigest.getInstance("SHA-256"));

        Lexer symbols = new Lexer(new InputStreamReader(digestStream));
        Parser p = new Parser(symbols);
        Program program = null;
        try {
            program = p.program();
        } catch (BadSyntax e) {
            System.err.println("Could not parse program! " + e);
            System.exit(1);
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (byte aByte : program.toString().getBytes()) {
            md.update(aByte);
        }

        byte[] dig = digestStream.getMessageDigest().digest();
        byte[] dig2 = md.digest();

        System.out.println("source:    " + String.format("%0" + (dig.length << 1) + "x", new BigInteger(1, dig)));
        System.out.println("formatted: " + String.format("%0" + (dig2.length << 1) + "x", new BigInteger(1, dig2)));

        if (!Arrays.equals(dig, dig2)) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
