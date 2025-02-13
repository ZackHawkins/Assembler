import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConverterTest {

    @Test
    public void test_file() {
        try (BufferedReader reader = new BufferedReader(new FileReader("test_instructions.txt")))
        {
            int fileLine = 0;
            String line;
            while((line = reader.readLine()) != null){
                String instruction = line.substring(line.indexOf(" "));
                String answer = line.substring(0, line.indexOf(" "));
                Converter test = new Converter(instruction);
                assertEquals(answer, test.instruction_to_hex());
                System.out.println("Line " + fileLine++ + ": "+ answer + " Passed");
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}