import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ConverterTest {

    @Test
    public void returnProperList(){
        String instruction = "     add        $s1,               $s2, $t5      ";
        Converter testList = new Converter(instruction);
        ArrayList<String> answer = new ArrayList<>();
        answer.add("add");
        answer.add("$s1");
        answer.add("$s2");
        answer.add("$t5");
        assertEquals(answer, testList.parseInstruction());
    }

}