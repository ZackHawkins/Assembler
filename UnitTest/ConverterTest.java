import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.FileReader;


public class ConverterTest {
    Converter test = new Converter();

    @Test
    public void convert_instruction_to_hex_test() {
        try (BufferedReader reader = new BufferedReader(new FileReader("test_instructions.txt")))
        {
            int fileLine = 1;
            String line;
            while((line = reader.readLine()) != null){
                String instruction = line.substring(line.indexOf(" "));
                String answer = line.substring(0, line.indexOf(" "));
                test.new_instruction(instruction);
                assertEquals(answer, test.instruction_to_hex());
                System.out.println("Line " + fileLine++ + ": "+ answer + " Passed");
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void test_li_pseudo_instruction(){
        test.new_instruction("li $v0, 0x3BF20");
        System.out.println(test.instruction_to_hex());
    }

    @Test
    public void test_la_pseudo_instruction(){
        test.new_instruction("la $a0, input_request");
        System.out.println(test.instruction_to_hex());
    }

    @Test
    public void test_move_pseudo_instruction(){
        test.new_instruction("move $a0, $a1");
        System.out.println(test.instruction_to_hex());
    }
}