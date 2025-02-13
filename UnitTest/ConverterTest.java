import org.junit.Test;
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

//------------------------------------------------------ J-Type ------------------------------------------------------//

    @Test
    public void j1(){
        String instruction = "j 0x84";
        Converter testList = new Converter(instruction);
        assertEquals("08000084", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

    @Test
    public void j2(){
        String instruction = "j 0xa5# Comment";
        Converter testList = new Converter(instruction);
        assertEquals("080000a5", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

    @Test
    public void j3(){
        String instruction = "j 0x4d";
        Converter testList = new Converter(instruction);
        assertEquals("0800004d", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

    @Test
    public void j4(){
        String instruction = "j 0x48# Comment";
        Converter testList = new Converter(instruction);
        assertEquals("08000048", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }

    @Test
    public void j5(){
        String instruction = "j 0x6f# Comment";
        Converter testList = new Converter(instruction);
        assertEquals("0800006f", testList.instruction_to_hex());
        System.out.println(testList.instruction_to_hex());
    }
}