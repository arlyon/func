package func;

import func.syntax.Program;
import func.visitors.MIPSCompiler;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {

    @Test
    void test() {
        ClassLoader classLoader = getClass().getClassLoader();
        Program p = Func.parseProgram(classLoader.getResourceAsStream("simple.func"));
        MIPSCompiler comp = new MIPSCompiler();
        comp.visit(p);
        Scanner scanner = new Scanner(classLoader.getResourceAsStream("simple.asm"));
        String expected = scanner.useDelimiter("\\A").next();
        assertEquals(expected, comp.toString());
    }
}
