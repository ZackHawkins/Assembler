import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        String fileLocation = "../../../" + args[0]; //backtracking to file location in the Assembler directory, not the jar directory
        new TextConverter(fileLocation);
    }
}
