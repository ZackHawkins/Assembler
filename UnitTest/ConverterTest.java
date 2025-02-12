import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ConverterTest {

    @Test
    public void returnProperList01(){
        String instruction = "     addiu        $s1,               $s2, 0x001f      ";
        Converter testList = new Converter(instruction);
        System.out.println(testList.get_instruction_array());
        System.out.println(testList.get_format_type());
    }

}