import org.junit.*;
import java.io.*;

public class Assembly {

    private boolean run_comparison(String inFile, String outFile, String answer){
        boolean passed = true;
        try(
                BufferedReader reader = new BufferedReader(new FileReader(outFile));
                BufferedReader answerReader = new BufferedReader(new FileReader(answer));
        )
        {
            String line1 = "";
            String line2 = "";
            int line = 1;
            while((line1 = reader.readLine()) != null && (line2 = answerReader.readLine()) != null){
                if(line1.equals(line2)){
                    System.out.println("Line: " + line + " " + line1 + " Passed");
                } else {
                    System.out.println("Line: " + line + " " + line1 + " Failed: " + line1 + " != " + line2);
                    passed = false;
                }
                ++line;
            }
        } catch(IOException io){
            System.out.println(io.getMessage() + "\n" + io.getCause());
        }
        return passed;
    }

    @Test
    public void Instruction_text_test(){
        String inFile = "Instructions.asm";
        String outFile = "Instructions.text";
        String answerFile = "InstructionsTextAnswer.txt";
        new TextConverter(inFile);
        System.out.println("Instruction.asm Text Test");
        assert run_comparison(inFile, outFile, answerFile);

        System.out.println("-----------------------------------------------------\n");
    }

    @Test
    public void Instruction_data_test(){
        String inFile = "Instructions.asm";
        String outFile = "Instructions.data";
        String answerFile = "InstructionsDataAnswer.txt";
        new TextConverter(inFile);
        System.out.println("Instruction.asm Data Test");
        assert run_comparison(inFile, outFile, answerFile);

        System.out.println("-----------------------------------------------------\n");
    }

    @Test
    public void EvenOrOdd_text_test(){
        String inFile = "EvenOrOdd.asm";
        String outFile = "EvenOrOdd.text";
        String answerFile = "EvenOrOddTextAnswer.txt";
        new TextConverter(inFile);
        System.out.println("EvenOrOdd.asm Text Test");
        assert run_comparison(inFile, outFile, answerFile);
        System.out.println("-----------------------------------------------------\n");
    }

    @Test
    public void EvenOrOdd_data_test(){
        String inFile = "EvenOrOdd.asm";
        String outFile = "EvenOrOdd.data";
        String answerFile = "EvenOrOddDataAnswer.txt";
        new TextConverter(inFile);
        System.out.println("EvenOrOdd.asm Data Test");
        assert run_comparison(inFile, outFile, answerFile);
        System.out.println("-----------------------------------------------------\n");
    }
}
