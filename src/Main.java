import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        //Converter op = new Converter(args[0].strip());
    }
}

class Converter {

    private final HashMap<String, Integer> mnemonic = new HashMap<>(); //hashmap for instruction opcode and corresponding binary value (6-Bit)
    private final HashMap<String, Integer> function = new HashMap<>(); //hashmap for function binary value (6-Bit) for R-Type

    private int
            /*operation instruction*/                   op_code,
            /*first operand*/                           rs,
            /*second operand*/                          rt,
            /*register destination (R-type)*/           rd,
            /*shift amount (R-type)*/                   shamt,
            /*function code (R-type)*/                  funct,
            /*constant (I-type)*/                       constant,
            /*address (I-type)*/                        address,
            /*classifying instruction I=0, R=1, J=2*/   format;

    private final String instruction; //instruction from the command line

    /**
     * specifying constructor sets the instruction variable from what was passed
     * in as a string
     * @param instruction
     */
    public Converter(String instruction){
        this.instruction = instruction;
        load_mnemonic();
        System.out.println(parseInstruction()); // <- this is where the print of the list happens (for testing)
    }

    /**
     * loading the mnemonic hashmap with the op code of
     * the associated instruction and calls the other
     * hashmap loader
     * @calls load_function
     */
    private void load_mnemonic(){
        mnemonic.put("add", 0);
        mnemonic.put("addiu", 9);
        mnemonic.put("and", 0);
        mnemonic.put("andi", 12);
        mnemonic.put("beq", 4);
        mnemonic.put("bne", 5);
        mnemonic.put("j", 2);
        mnemonic.put("lui", 15);
        mnemonic.put("lw", 35);
        mnemonic.put("or", 0);
        mnemonic.put("ori", 13);
        mnemonic.put("slt", 0);
        mnemonic.put("sub", 0);
        mnemonic.put("sw", 43);
        mnemonic.put("syscall", 0);
        load_function();
    }

    /**
     * loading the function hashmap with the func of the
     * associated instruction
     */
    private void load_function(){
        function.put("add", 32);
        function.put("and", 36);
        function.put("or", 37);
        function.put("slt", 42);
        function.put("sub", 34);
    }

    /**
     * Tokenizes a string (instruction) and appends each valid token to
     * a list. Returns tokenized list once the end of the instruction string
     * is reached or when the comment character '#' is reached
     * @delimiter COMMA, SPACE
     * @return Tokenized ArrayList<String>
     */
    private ArrayList<String> parseInstruction(){
        StringTokenizer tokenizer = new StringTokenizer(instruction, " ,");
        ArrayList<String> instructionList = new ArrayList<>();
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            if(token.contains("#")){
                int cutOff = token.indexOf('#');
                token = token.substring(0, cutOff);
                if(!token.isEmpty()){instructionList.add(token);}
                break;
            }
            instructionList.add(token);
        }
        return instructionList;
    }
}