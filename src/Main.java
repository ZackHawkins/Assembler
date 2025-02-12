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
        load_mnemonic(); //loads both hashmaps
        this.instruction = instruction.toLowerCase();
        this.instructionArray = parse_instruction();
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
     */
    private ArrayList<String> parse_instruction(){
        StringTokenizer tokenizer = new StringTokenizer(instruction, " ,");
        ArrayList<String> temp = new ArrayList<>();
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            if(token.contains("#")){ //checks to see if '#' is attached to one of the valid instruction pieces, '#' = start of comment
                token = token.substring(0, token.indexOf('#')); //detaches '#' from valid instruction piece
                if(!token.isEmpty()){temp.add(token);} //add only the valid instruction piece, if token was only '#' -> token would be EMPTY
                break;
            }
            temp.add(token);
        }
        try {
            int lastElement = temp.size() - 1; //last element in arrayList
            if (temp.get(lastElement).contains("0x")) {temp.set(lastElement, hex_to_decimal(temp.get(lastElement).substring(2)));} //2 is the offset since it is in hexadecimal '0x..'
        } catch (IndexOutOfBoundsException ioube){
            System.out.println(ioube.getMessage());
        }
        return temp;
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
        try {
            if (instructionArray.get(0).equals("j")) {
                return "J-type";
            } else if (function.containsKey(instructionArray.get(0))) {
                return "R-type";
            }
            return "I-type";
        } catch (IndexOutOfBoundsException iobe) {
            System.out.println(iobe.getMessage());
        }
        return null;
    }

    /**
     * returns the format type for the passed in assembly instruction
     * @return format type
     */
    public String get_format_type(){return this.format;}

    /**
     * returns the arraylist that contains the contents of the passed in assembly
     * instruction. Each index of the array holds a single piece of passed in assembly instruction
     * @return arraylist containing the parts to the passed in assembly instruction
     */
    public ArrayList<String> get_instruction_array(){return this.instructionArray;}

    public String instruction_to_hex(){
        return switch (format) {
            case "R-type" -> format_r_type_converter();
            case "I-type" -> format_i_type_converter();
            case "J-type" -> format_j_type_converter();
            default -> null;
        };
    }

    private String format_i_type_converter() {
        return null;
    }

    private String format_r_type_converter() {
        switch (instructionArray.get(0)){
            case "syscall":
                break;
            default:
                this.op_code = mnemonic.get(instructionArray.get(0));
                System.out.println(get_register_value(instructionArray.get(1)));
        }

        return null;
    }

    private String format_j_type_converter(){
        return null;
    }

    private int get_register_value(String reg){
        if(!reg.contains("$")){throw new IllegalArgumentException("Passed in value must be register '$...'\n");}
        int regValue = 0;
        switch(reg){
            case "$zero": return 0;
            case "$at": return 1;
            case "$gp": return 28;
            case "$sp": return 29;
            case "$fp": return 30;
            case "$ra": return 31;
            default:
                String loc = reg.substring(0,2);
                int offSet = Integer.parseInt(reg.substring(2));
                switch(loc){
                    case "$v":
                        regValue = 2 + offSet;
                        break;
                    case "$a":
                        regValue = 4 + offSet;
                        break;
                    case "$t":
                        if(offSet < 8){regValue = 8 + offSet;}
                        else{regValue = 24 + (offSet - 8);}
                        break;
                    case "$s":
                        regValue = 16 + offSet;
                        break;
                    case "$k":
                        regValue = 26 + offSet;
                        break;
                }
        }
        return regValue;
    }

}