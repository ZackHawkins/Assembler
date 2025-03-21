import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Converter {

    private final HashMap<String, Integer> mnemonic; //hashmap for instruction opcode and corresponding binary value (6-Bit)
    private final HashMap<String, Integer> function; //hashmap for function binary value (6-Bit) for R-Type
    private ArrayList<String> instructionArray; //passed in instruction parsed into an array
    private String instruction; //instruction from the command line
    private final HashMap<String, Integer> data;

//------------------------------------------------------ Public Method Calls ------------------------------------------------------//

    /**
     * returns assembly instruction as a String in hexadecimal notation
     *
     * @return String
     */
    public String instruction_to_hex() {
        return switch (get_format_type()) {
            case "R-type" -> format_r_type_converter();
            case "I-type" -> format_i_type_converter();
            case "J-type" -> format_j_type_converter();
            case "Pseudo-Instruction" -> pseudo_instruction();
            default -> null;
        };
    }

    /**
     * determines what type of instruction is being called upon.
     * It could be I-type, R-type, J-type or syscall
     */
    public String get_format_type() {
        try {
            if (instructionArray.get(0).equals("j")) {
                return "J-type";
            } else if (function.containsKey(instructionArray.get(0))) {
                return "R-type";
            } else if (
                    instructionArray.get(0).equals("li")
                            || instructionArray.get(0).equals("la")
                            || instructionArray.get(0).equals("blt")
                            || instructionArray.get(0).equals("move")
            ) return "Pseudo-Instruction";
            return "I-type";
        } catch (IndexOutOfBoundsException iobe) {
            System.out.println(iobe.getMessage());
        }
        return null;
    }

    /**
     * new_instruction is a public method created for efficiency reasons. This method is to allow the
     * same converter object to convert a new set of instructions to hexa-decimal
     *
     * @param instruction String
     */
    public void new_instruction(String instruction) {
        this.instruction = instruction.toLowerCase();
        this.instructionArray.clear();
        parse_instruction();
    }

//------------------------------------------------------ Environment Setup ------------------------------------------------------//

    /**
     * zero-parameter constructor will call the specifying constructor with "null" being the
     * passed in parameter
     */
    public Converter() {
        this("null");
    }

    /**
     * specifying constructor sets the instruction variable from what was passed
     * in as a string
     *
     * @param instruction String
     */
    public Converter(String instruction) {
        this.mnemonic = new HashMap<String, Integer>();
        this.function = new HashMap<String, Integer>();
        this.instructionArray = new ArrayList<String>();
        this.data = DataConverter.processAsmFile("EvenOrOdd.asm");
        load_mnemonic(); //loads both hashmaps
        new_instruction(instruction);
    }

    /**
     * loading the mnemonic hashmap with the op code of
     * the associated instruction and calls the other
     * hashmap loader
     *
     * @calls load_function
     */
    private void load_mnemonic() {
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
    private void load_function() {
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
     *
     * @delimiter COMMA, SPACE
     */
    private void parse_instruction() {
        StringTokenizer tokenizer = new StringTokenizer(instruction, " ,");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.contains("#")) { //checks to see if '#' is attached to one of the valid instruction pieces, '#' = start of comment
                token = token.substring(0, token.indexOf('#')); //detaches '#' from valid instruction piece
                if (!token.isEmpty()) {
                    this.instructionArray.add(token);
                } //add only the valid instruction piece, if token was only '#' -> token would be EMPTY
                break;
            }
            this.instructionArray.add(token);
        }
        try {
            int lastElement = this.instructionArray.size() - 1; //last element in arrayList
            if (this.instructionArray.get(lastElement).contains("0x")) {
                this.instructionArray.set(lastElement, hex_to_decimal(this.instructionArray.get(lastElement).substring(2)));
            } //Converts last instruction value to integer if it is a hexadecimal, 2 is the offset since it is in hexadecimal '0x..'
        } catch (IndexOutOfBoundsException ioube) {
            System.out.println(ioube.getMessage());
        }
    }

//------------------------------------------------------ Converters ------------------------------------------------------//

    /**
     * returns the decimal value of a hexadecimal as a String
     * @param hex String
     * @return hexadecimal converted into decimal notation and returned as a String
     */
    private String hex_to_decimal(String hex) {
        return String.valueOf(Integer.parseInt(hex, 16));
    }

    /**
     * decimal_to_hex is a method that take sin an integer and returns the hexadecimal notation in
     * base 16 format
     * @param decimal int
     * @return a string of the hexadecimal notation from the passed in integer
     */
    private String decimal_to_hex(int decimal) {
        return Integer.toHexString(decimal);
    }

    private String pseudo_instruction(){
        String answer = "";
        String register = this.instructionArray.get(1);
        String register2 = "";
        int immediate = 0;
        String inst = this.instructionArray.get(0);
        switch (inst) {
            case "move" -> register2 = this.instructionArray.get(2);
            case "la" -> immediate = this.data.get(this.instructionArray.get(2));
            case "li" -> immediate = Integer.parseInt(this.instructionArray.get(2));
        }
        this.instructionArray.clear();
        switch(inst){
            case "li","la":
                if(immediate <= 0xFFFF){
                    this.instructionArray.add("addiu");
                    this.instructionArray.add(register);
                    this.instructionArray.add("$zero");
                    this.instructionArray.add(Integer.toString(immediate));
                    answer = instruction_to_hex();
                } else {
                    this.instructionArray.add("lui");
                    this.instructionArray.add("$at");
                    this.instructionArray.add(Integer.toString(immediate >> 16));
                    answer += instruction_to_hex();
                    answer += "\n";
                    this.instructionArray.set(0, "ori");
                    this.instructionArray.set(1, register);
                    this.instructionArray.set(2, "$at");
                    this.instructionArray.add(Integer.toString(immediate & 0xFFFF));
                    answer += instruction_to_hex();
                }
            case "move":
                this.instructionArray.add("add");
                this.instructionArray.add(register);
                this.instructionArray.add(register2);
                this.instructionArray.add("$zero");
        }       answer = instruction_to_hex();
        return answer;
    }


     /**
     * helper method to for format_i_type_converter, this method will return a specific string
     * depending on opcode. That string is used in format_i_type_converter to determine what order
     * our variables need to check the array
     * @param op opcode string type
     * @return coded string
     */
    private String rs_rt_order(String op){
        return switch (op) {
            case "addiu", "andi", "ori" -> "rt_rs";
            case "beq", "bne" -> "rs_rt";
            case "lui" -> "lui";
            case "lw", "sw" -> "rt off(base)";
            default -> "";
        };
    }

    /**
     * converts I-type assembly instruction into hexadecimal notation
     * @return String of hexadecimal
     */
    private String format_i_type_converter() {
        int opCode = 0, rt = 0, rs = 0, immediate = 0, instruction = 0;
        switch (rs_rt_order(instructionArray.get(0))){
            case "rt_rs":
                opCode = mnemonic.get(instructionArray.get(0));
                rt = get_register_value(instructionArray.get(1));
                rs = get_register_value(instructionArray.get(2));
                immediate = Integer.parseInt(instructionArray.get(3)) & 0xFFFF;
                break;
            case "rs_rt":
                opCode = mnemonic.get(instructionArray.get(0));
                rs = get_register_value(instructionArray.get(1));
                rt = get_register_value(instructionArray.get(2));
                immediate = Integer.parseInt(instructionArray.get(3)) & 0xFFFF;
                break;
            case "lui":
                opCode = mnemonic.get(instructionArray.get(0));
                rt = get_register_value(instructionArray.get(1));
                immediate = Integer.parseInt(instructionArray.get(2)) & 0xFFFF;
                break;
            case "rt off(base)":
                opCode = mnemonic.get(instructionArray.get(0));
                rt = get_register_value(instructionArray.get(1));
                rs = get_register_value(instructionArray.get(2).substring(instructionArray.get(2).indexOf('(') + 1,instructionArray.get(2).indexOf(')')));
                String check = instructionArray.get(2).substring(0,instructionArray.get(2).indexOf('('));
                if(!check.isEmpty()){
                    immediate = Integer.parseInt(check) & 0xFFFF;
                }
        }

        instruction |= immediate;
        instruction |= (rt << 16);
        instruction |= (rs << 21);
        instruction |= (opCode << 26);

        return String.format("%08x", instruction);

    }

    /**
     * converts the R-type assembly instruction into hexadecimal notation
     * @return String
     */
    private String format_r_type_converter() {
        int rformat = 0;
        if (instructionArray.get(0).equals("syscall")) {
            rformat |= function.get(instructionArray.get(0));
            rformat |= (mnemonic.get(instructionArray.get(0)) << 6);
        } else {
            int op_code = mnemonic.get(instructionArray.get(0));
            int rd = get_register_value(instructionArray.get(1));
            int rs = get_register_value(instructionArray.get(2));
            int rt = get_register_value(instructionArray.get(3));
            int shamt = 0;
            int funct = function.get(instructionArray.get(0));
            rformat |= (funct);
            rformat |= (shamt << 6); //always 0
            rformat |= (rd << 11);
            rformat |= (rt << 16);
            rformat |= (rs << 21);
            rformat |= (op_code << 26);
        }
        return String.format("%08x", rformat);
    }

    /**
     * converts the J-type assembly instruction into hexadecimal notation
     * @return String
     */
    private String format_j_type_converter(){
        int jformat = 0;
        jformat |= (Integer.parseInt(instructionArray.get(1)));
        jformat |= (mnemonic.get(instructionArray.get(0)) << 26);
        return String.format("%08x", jformat);
    }

    /**
     * returns the corresponding integer value of the passed in assembly
     * register name
     * @param reg String - assembly register name
     * @return int
     */
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
                int offSet = Integer.parseInt(reg.substring(2));
                switch(reg.substring(0,2)){
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