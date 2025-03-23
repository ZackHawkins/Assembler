import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TextConverter {

    private final int BASE_TEXT_ADDRESS = 0x00400000;
    private int currentAddress;
    private HashMap<String,Integer> data;
    private InstructionConverter converter;
    private HashMap<String, Integer> labels;

    public TextConverter(String inFile){
        this.data = DataConverter.processAsmFile(inFile);
        this.converter = new InstructionConverter();
        this.currentAddress = BASE_TEXT_ADDRESS;
        this.labels = new HashMap<String, Integer>();
        process_asm_file(inFile);
    }

    private boolean is_pseudo_branch_instruction(String instruction){
        return switch(instruction){
            case "li", "la", "move", "blt","j","bne","beq" -> true;
            default -> false;
        };
    }

    private void calculate_number_of_pseudo_instruction(ArrayList<String> instructionList){
        String inst = instructionList.get(0);
        int immediate = 0;
        switch(inst){
            case "la" -> immediate = data.get(instructionList.get(2));
            case "li" -> immediate = Integer.parseInt(instructionList.get(2));
        }
        switch(inst){
            case "li","la":
                if(immediate <= 0xFFFF) this.currentAddress += 0x00000004;
                else this.currentAddress += 0x00000008;
                break;
            case "move":
                this.currentAddress += 0x00000004;
                break;
            case "blt":
                this.currentAddress += 0x00000008;
                break;
            default: this.currentAddress += 0x00000004;
        }
    }

    private void get_asm_labels(String inFile){
        boolean textSection = false;
        String line;
        ArrayList<String> currentInstruction;
        Pattern pattern = Pattern.compile("\\b\\w+:");
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
                    this.currentAddress += 0x00000004;
                }
            }
        } catch (IOException io){
            System.out.println(io.getMessage() + "\n\n" + io.getCause());
        }
    }

    private String pseudo_branch_instruction(ArrayList<String> instructionArray){
        String answer = "";
        String inst = instructionArray.get(0);
        String register = instructionArray.get(1);
        String register2 = "";
        int label = 0;
        int immediate = 0;
        int offset = 0;
        ArrayList<String> newInstruction = new ArrayList<>();

        switch(inst){
            case "move" -> register2 = instructionArray.get(2);
            case "la" -> immediate = this.data.get(instructionArray.get(2));
            case "li" -> immediate = Integer.parseInt(instructionArray.get(2));
            case "blt" -> {register2 = instructionArray.get(2); offset = ((this.labels.get(instructionArray.get(3)) - (this.currentAddress + 4)) / 4);}
            case "j" -> label = this.labels.get(instructionArray.get(1));
            case "bne", "beq" -> {offset = ((this.labels.get(instructionArray.get(3)) - (this.currentAddress + 4)) / 4); register2=instructionArray.get(2);}
        }

        switch(inst){
            case "li", "la":
                if(immediate <= 0xFFFF){
                    newInstruction.add("addiu");
                    newInstruction.add(register);
                    newInstruction.add("$zero");
                    newInstruction.add(Integer.toString(immediate));
                    this.converter.new_instruction(newInstruction);
                    answer = this.converter.instruction_to_hex();
                } else {
                    newInstruction.add("lui");
                    newInstruction.add("$at");
                    newInstruction.add(Integer.toString(immediate >> 16));
                    this.converter.new_instruction(newInstruction);
                    answer = this.converter.instruction_to_hex();
                    answer += "\n";
                    newInstruction.set(0, "ori");
                    newInstruction.set(1, register);
                    newInstruction.set(2, "$at");
                    newInstruction.add(Integer.toString(immediate & 0xFFFF));
                    this.converter.new_instruction(newInstruction);
                    answer += this.converter.instruction_to_hex();
                }
                break;
            case "move":
                newInstruction.add("add");
                newInstruction.add(register);
                newInstruction.add(register2);
                newInstruction.add("$zero");
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
               newInstruction.add(Integer.toString(offset));
               this.converter.new_instruction(newInstruction);
               answer += this.converter.instruction_to_hex();
               break;
            case "j":
                newInstruction.add("j");
                newInstruction.add(Integer.toString(label >> 2));
                this.converter.new_instruction(newInstruction);
                answer = this.converter.instruction_to_hex();
                break;
            case "beq","bne":
                newInstruction.add("beq");
                newInstruction.add(register);
                newInstruction.add(register2);
                newInstruction.add(Integer.toString(offset));
                this.converter.new_instruction(newInstruction);
                answer = converter.instruction_to_hex();
                break;
        }
        return answer;
    }

    private void process_asm_file(String inFile){
        try(
             BufferedReader reader = new BufferedReader(new FileReader(inFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(inFile.replaceAll("\\.asm$", "\\.text")));
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
        } catch(IOException io){
            System.out.println(io.getMessage() + "\n\n" + io.getCause());
        }
    }
}
