import java.util.Scanner;

public class {{ fileName }} {

    private static Integer times(Integer x, Integer y) {
        return x * y;
    }

    private static Integer plus(Integer x, Integer y) {
        return x + y;
    }

    private static void write(Integer x) {
        System.out.println(x);
    }

    private static Integer read() {
        Scanner in = new Scanner(System.in);
        return in.nextInt();
    }

    {{{ methods }}}

    {{{ main }}}
}
