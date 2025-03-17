import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        Converter op = new Converter(args[0]);
        System.out.println(op.instruction_to_hex());
    }
}