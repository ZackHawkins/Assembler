import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ConverterTest {

    @Test
    public void returnProperList01(){
        String instruction = "add $ra $s1 $t1";
        Converter testList = new Converter(instruction);
        testList.instruction_to_hex();
    }

}