import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TextConverter {

    private final int BASE_TEXT_ADDRESS = 0x00400000;
    private int currentAddress;
    private HashMap<String,Integer> data;
    private InstructionConverter converter;
    private HashMap<String, Integer> labels;

//------------------------------------------------------ Public Method Calls ------------------------------------------------------//

    /**
     * TextConverter is the specifying constructor that takes in a string representation
     * of the assembly file to read
     * @param inFile assembly file
     */
    public TextConverter(String inFile){
        inFile = inFile.trim();
        this.data = DataConverter.processAsmFile(inFile);
        this.converter = new InstructionConverter();
        this.currentAddress = BASE_TEXT_ADDRESS;
        this.labels = new HashMap<String, Integer>();
        process_asm_file(inFile);
    }

//------------------------------------------------------ File Reading/Interpreting  ------------------------------------------------------//

    /**
     * is_pseudo_branch_instruction determines if the passed in instruction is a pseudo instruction, an instruction
     * that uses one or more other instructions to replace the current instruction, or if the instruction is of
     * a branch/jump type
     * @param instruction instruction
     * @return true -> instruction is of pseudo, branch, or jump instruction type
     */
    private boolean is_pseudo_branch_instruction(String instruction){
        return switch(instruction){
            case "li", "la", "move", "blt","j","bne","beq" -> true;
            default -> false;
        };
    }

    /**
     * calculate_number_of_pseudo_instruction is a method that determines how the current address will be altered
     * given the current instruction passed in. This is determined by how many instructions are needed to replace
     * the single pseudo-instruction
     * @param instructionList ArrayList<String> of the current pseudo-instruction
     */
    private void calculate_number_of_pseudo_instruction(ArrayList<String> instructionList){
        String inst = instructionList.get(0);
        int immediate = 0;
        switch(inst){
            case "la" -> immediate = data.get(instructionList.get(2));
            case "li" -> immediate = Integer.parseInt(instructionList.get(2));
        }
        switch(inst){
            case "li","la":
                if(immediate <= 0xFFFF) this.currentAddress += 4;
                else this.currentAddress += 8;
                break;
            case "move":
                this.currentAddress += 4;
                break;
            case "blt":
                this.currentAddress += 8;
                break;
            default: this.currentAddress += 4; //is a branch instruction or a jump instruction
        }
    }

    /**
     * get_asm_labels is a method that reads the assembly file prior to writing the files instructions in hexadecimal
     * notation. This method reads the file and solely looks for labels in the assembly file and determines each labels
     * address location and stores it in a HashMap.
     * @param inFile assembly file to read
     */
    private void get_asm_labels(String inFile){
        boolean textSection = false;
        String line;
        ArrayList<String> currentInstruction;
        Pattern pattern = Pattern.compile("\\b\\w+:"); //pattern the labels will contain
        try(BufferedReader reader = new BufferedReader(new FileReader(inFile));){
            while((line = reader.readLine()) != null){
                line = line.trim();

                if(line.equals(".text")){textSection = true; continue;}
                if(line.isEmpty() || line.startsWith("#") || !textSection) continue;

                Matcher matcher = pattern.matcher(line);

                if(matcher.matches()){
                    this.labels.put(line.substring(0, line.indexOf(':')), this.currentAddress);
                    continue;
                }

                this.converter.new_instruction(line);
                currentInstruction = this.converter.get_instruction_array();
                if(is_pseudo_branch_instruction(currentInstruction.get(0))){
                    calculate_number_of_pseudo_instruction(currentInstruction);
                } else {
                    this.currentAddress += 4;
                }
            }
        } catch (IOException io){
            System.out.println(io.getMessage() + "\n\n" + io.getCause());
        }
    }

    /**
     * calculate_offset is a method used for determining the offset of labels used in branch instructions
     * @param label current label being used in instruction
     * @param instruction op
     * @return integer of the offset from current instruction to the label that is being jumped to
     */
    private int calculate_offset(String label, String instruction){
        int offset = 4;
        if(instruction.equals("blt")) offset = 8;
        return (this.labels.get(label) - (this.currentAddress + offset)) / 4;
    }

    /**
     * pseudo_branch_instruction is a method that returns the hexadecimal notation of the current instruction. This
     * method is used for pseudo, branch, and jump instructions. The method will replace the proper instructions for
     * InstructionConverter to interpret and perform the hexadecimal calculations on.
     * @param instructionArray current instruction
     * @return Hexadecimal String interpretation of the current instruction
     */
    private String pseudo_branch_instruction(ArrayList<String> instructionArray){
        String answer = "";
        String inst = instructionArray.get(0); //instruction
        String register = instructionArray.get(1);
        String register2 = "";
        int value = 0;
        ArrayList<String> newInstruction = new ArrayList<>();

        switch(inst){
            case "move" -> register2 = instructionArray.get(2);
            case "la" -> value = this.data.get(instructionArray.get(2));
            case "li" -> value = Integer.parseInt(instructionArray.get(2));
            case "bne","beq", "blt" -> {
                register2 = instructionArray.get(2);
                value = calculate_offset(instructionArray.get(3), inst);
            }
            case "j" -> value = this.labels.get(instructionArray.get(1)) >> 2;
        }

        switch(inst){
            case "li", "la":
                if(value <= 0xFFFF){ //if the value's value is smaller than 16-bits
                    newInstruction.add("addiu");
                    newInstruction.add(register);
                    newInstruction.add("$zero");
                    newInstruction.add(Integer.toString(value));
                    this.converter.new_instruction(newInstruction);
                    answer = this.converter.instruction_to_hex();
                } else {
                    newInstruction.add("lui");
                    newInstruction.add("$at");
                    newInstruction.add(Integer.toString(value >> 16));
                    this.converter.new_instruction(newInstruction);
                    answer = this.converter.instruction_to_hex();
                    answer += "\n";
                    newInstruction.set(0, "ori");
                    newInstruction.set(1, register);
                    newInstruction.set(2, "$at");
                    newInstruction.add(Integer.toString(value & 0xFFFF));
                    this.converter.new_instruction(newInstruction);
                    answer += this.converter.instruction_to_hex();
                }
                break;
            case "move":
                newInstruction.add("addu");
                newInstruction.add(register);
                newInstruction.add("$zero");
                newInstruction.add(register2);
                this.converter.new_instruction(newInstruction);
                answer = this.converter.instruction_to_hex();
                break;
            case "blt":
               newInstruction.add("slt");
               newInstruction.add("$at");
               newInstruction.add(register);
               newInstruction.add(register2);
               this.converter.new_instruction(newInstruction);
               answer = this.converter.instruction_to_hex();
               answer += "\n";
               newInstruction.set(0, "bne");
               newInstruction.set(1, "$at");
               newInstruction.set(2, "$zero");
               newInstruction.set(3, Integer.toString(value));
               this.converter.new_instruction(newInstruction);
               answer += this.converter.instruction_to_hex();
               break;
            case "j":
                newInstruction.add("j");
                newInstruction.add(Integer.toString(value));
                this.converter.new_instruction(newInstruction);
                answer = this.converter.instruction_to_hex();
                break;
            case "beq","bne":
                newInstruction.add(inst);
                newInstruction.add(register);
                newInstruction.add(register2);
                newInstruction.add(Integer.toString(value));
                this.converter.new_instruction(newInstruction);
                answer = converter.instruction_to_hex();
                break;
        }
        return answer;
    }

    /**
     * process_asm_file reads the passed in assembly file and writes the .text file for the passed in assembly file.
     * The .text file will contain all the instructions used, pseudo-instructions will be replaced with the proper
     * instruction set, written in their hexadecimal notation
     * @param inFile Assembly file to read and interpret
     */
    private void process_asm_file(String inFile){
        String outFile = inFile.replaceAll("\\.asm$", "\\.text");
        try(
             BufferedReader reader = new BufferedReader(new FileReader(inFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            )
        {
                ArrayList<String> instructionList = new ArrayList<>();
                get_asm_labels(inFile);
                this.currentAddress = BASE_TEXT_ADDRESS;
                String line;
                String instruction;
                boolean textSection = false;
                Pattern pattern = Pattern.compile("\\b\\w+:");

                while((line = reader.readLine()) != null){
                    line = line.trim();

                    if(line.contains(".text")){textSection = true; continue;}
                    if(line.isEmpty() || line.startsWith("#") || !textSection) continue;

                    Matcher matcher = pattern.matcher(line);
                    if(matcher.matches()) continue;

                    this.converter.new_instruction(line);
                    instructionList.clear();
                    instructionList.addAll(this.converter.get_instruction_array());
                    if(is_pseudo_branch_instruction(instructionList.get(0))){
                        instruction = pseudo_branch_instruction(instructionList);
                        calculate_number_of_pseudo_instruction(instructionList);
                        writer.write(instruction);
                    } else {
                        writer.write(this.converter.instruction_to_hex());
                        this.currentAddress += 0x00000004;
                    }
                    writer.newLine();
                }
                System.out.println("Text Section has been converted and saved to " + outFile.substring(outFile.lastIndexOf('/')+1));
        } catch(IOException io){
            System.out.println(io.getMessage() + "\n\n" + io.getCause());
        }
    }
}
