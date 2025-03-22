import org.junit.Test;

public class Assembly {

    @Test
    public void create_TextConverter_object_test(){
        TextConverter.process_asm_file("EvenOrOdd.asm");
    }
}
