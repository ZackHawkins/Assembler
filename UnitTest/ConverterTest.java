import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ConverterTest {

//------------------------------------------------------ R-Type ------------------------------------------------------//
    @Test
    public void syscall(){
        String instruction = "syscall";
        Converter testList = new Converter(instruction);
        assertEquals("0000000c", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

    @Test
    public void sub(){
        String instruction = "sub $t5, $s1, $s2";
        Converter testList = new Converter(instruction);
        assertEquals("02326822", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

    @Test
    public void add(){
        String instruction = "add $a1, $t8, $t9";
        Converter testList = new Converter(instruction);
        assertEquals("03192820", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

    @Test
    public void and(){
        String instruction = "and $t7, $t1, $a1";
        Converter testList = new Converter(instruction);
        assertEquals("01257824", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

    @Test
    public void or(){
        String instruction = "or $s5, $k1, $t7";
        Converter testList = new Converter(instruction);
        assertEquals("036fa825", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

    @Test
    public void slt(){
        String instruction = "slt $zero, $t4, $fp";
        Converter testList = new Converter(instruction);
        assertEquals("019e002a", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

//------------------------------------------------------    ------------------------------------------------------//

}