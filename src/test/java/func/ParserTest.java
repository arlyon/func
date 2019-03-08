package func;

import func.syntax.Program;
import func.visitors.ASTPrinter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Assertions;

import java.io.StringReader;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Iterator;

class ParserTest {

    static final String program1 = "method pow(x, y) vars i, res\n" +
        "begin\n" +
        "    res := x;\n" +
        "    i := 1;\n" +
        "    while less(i, y)\n" +
        "    begin\n" +
        "        res := times(res, x);\n" +
        "        i := plus(i, 1);\n" +
        "    endwhile;\n" +
        "    write res;\n" +
        "    return res;\n" +
        "endmethod;\n" +
        "\n" +
        "method main() vars a, b, x\n" +
        "begin\n" +
        "    a := 5;\n" +
        "    b := 2;\n" +
        "    x := pow(b, a);\n" +
        "    if eq(x, 32)\n" +
        "    then\n" +
        "        write 1;\n" +
        "    else\n" +
        "        write 0;\n" +
        "    endif;\n" +
        "endmethod;\n";

    static final String program2 = "method main() vars inp, res\n" +
        "begin\n" +
        "    read inp;\n" +
        "    res := 0;\n" +
        "    while less(0, inp)\n" +
        "    begin\n" +
        "        res := plus(res, inp);\n" +
        "        inp := minus(inp, 1);\n" +
        "    endwhile;\n" +
        "    write res;\n" +
        "endmethod;\n";

    static final String program3 = "method sum(inp) vars res\n" +
        "begin\n" +
        "    res := 0;\n" +
        "    while less(0, inp)\n" +
        "    begin\n" +
        "        res := plus(res, inp);\n" +
        "        inp := minus(inp, 1);\n" +
        "    endwhile;\n" +
        "    return res;\n" +
        "endmethod;\n" +
        "\n" +
        "method main() vars inp, res\n" +
        "begin\n" +
        "    read inp;\n" +
        "    res := sum(inp);\n" +
        "    write res;\n" +
        "endmethod;\n";

    @ParameterizedTest
    @ValueSource(strings = {program1, program2, program3})
    void test(String string) throws BadSyntax {
        Iterator<Token> symbols = new Lexer(new StringReader(string));
        Parser p = new Parser(symbols);
        Program program = p.program();
        ASTPrinter printer = new ASTPrinter();
        printer.visit(program);
        Assertions.assertEquals(printer.toString(), string);
        Assertions.assertEquals(program.toString(), string);
    }
}