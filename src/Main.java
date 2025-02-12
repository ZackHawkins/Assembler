import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        //Converter op = new Converter(args[0]);
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
            /*address (I-type)*/                        address;

    private final String format; //R-type, I-type, or J-type

    private final String instruction; //instruction from the command line
    private final ArrayList<String> instructionArray; //passed in instruction parsed into an array

    /**
     * specifying constructor sets the instruction variable from what was passed
     * in as a string
     * @param instruction String
     */
    public Converter(String instruction){
        this.instruction = instruction.toLowerCase();
        this.instructionArray = parseInstruction();
        load_mnemonic(); //calls load_function inside of load_mnemonic
        this.format = format_type();
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
     * associated instruction (R-type)
     */
    private void load_function(){
        function.put("add", 32);
        function.put("and", 36);
        function.put("or", 37);
        function.put("slt", 42);
        function.put("sub", 34);
        function.put("syscall", 12);
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
            if(token.contains("#")){ //checks to see if '#' is attached to one of the valid instruction pieces, '#' = start of comment
                token = token.substring(0, token.indexOf('#')); //detaches '#' from valid instruction piece
                if(!token.isEmpty()){instructionList.add(token);} //add only the valid instruction piece, if token was only '#' -> token would be EMPTY
                break;
            }
            instructionList.add(token);
        }
        int lastElement = instructionList.size() -1; //last element in arrayList
        if(instructionList.get(lastElement).contains("0x")){instructionList.set(lastElement, hex_to_decimal(instructionList.get(lastElement).substring(2)));} //2 is the offset since it is in hexadecimal '0x..'
        return instructionList;
    }

    /**
     * returns the decimal value of a hexadecimal as a String
     * @param hex String
     * @return hexadecimal converted into decimal notation and returned as a String
     */
    private String hex_to_decimal(String hex){return String.valueOf(Integer.parseInt(hex, 16));}//base 16

    /**
     * setFormat determines what type of instruction is being called upon.
     * It could be I-type, R-type, J-type or syscall
     */
    private String format_type(){
        if(instructionArray.get(0).equals("j")){return "J-type";}
        else if(function.containsKey(instructionArray.get(0))){return "R-type";}
        return "I-type";
    }

    public String get_format_type(){return this.format;}
    public ArrayList<String> get_instruction_array(){return this.instructionArray;}


}