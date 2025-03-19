import org.junit.Test;

public class TextTest {
    Converter test = new Converter();

    @Test
    public void psuedo_instruction(){
        test.new_instruction("li $a1, 0x3BF20");
        System.out.println(test.get_format_type());
        String hex = test.instruction_to_hex();
        System.out.println(hex);

    }
}
