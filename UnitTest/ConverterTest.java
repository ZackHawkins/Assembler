import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ConverterTest {

    @Test
    public void returnProperList01(){
        String instruction = "add#hello";
        Converter testList = new Converter(instruction);
        System.out.println(testList.get_instruction_array());
        System.out.println(testList.get_format_type());
    }

}