package func;

import func.syntax.Program;
import io.airlift.airline.*;

import java.io.*;
import java.util.Iterator;

import static java.lang.System.exit;

public class Func {
    public static void main(String[] args) {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("func")
            .withDescription("Tools for the func programming language")
            .withDefaultCommand(Help.class)
            .withCommands(Help.class, Format.class);

        Cli<Runnable> funcParser = builder.build();
        funcParser.parse(args).run();
    }

    @Command(name = "format", description = "Parses, validated, and formats a func file")
    public static class Format implements Runnable {

        @Arguments(description = "The input file")
        public String inFileName;

        @Option(name = "-o", description = "If specified, direct output to the provided file")
        public String outFileName;

        @Override
        public void run() {
            Iterator<FileToken> symbols = null;

            try {
                symbols = new Lexer(new FileReader(inFileName));
            } catch (FileNotFoundException e) {
                System.out.println("Error with your file: " + e.getLocalizedMessage());
                exit(1);
            }

            Parser p = new Parser(symbols);
            Program program = null;
            try {
                program = p.program();
            } catch (BadSyntax badSyntax) {
                badSyntax.printStackTrace();
                exit(1);
            }

            if (this.outFileName != null) {
                try {
                    FileWriter fw = new FileWriter(this.outFileName);
                    fw.write(program.toString());
                    fw.close();
                } catch (IOException e) {
                    System.out.println("Invalid output file "+e.getLocalizedMessage());
                    exit(1);
                }
            } else {
                System.out.println(program);
            }
        }
    }
}
