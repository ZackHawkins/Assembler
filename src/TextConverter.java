import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TextConverter {

    public static void process_asm_file(String inFile){
        try(
             BufferedReader reader = new BufferedReader(new FileReader(inFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(inFile.replaceAll("\\.asm$", "\\.text")));
            )
        {
                InstructionConverter converter = new InstructionConverter(inFile);
                HashMap<String, Integer> labels = label_determiner(inFile);
                int address = 0x00400000;
                String line;
                boolean hasTextSection = false;

                while((line = reader.readLine()) != null){
                    line = line.trim();

                    if(line.contains(".text")){hasTextSection = true; continue;}
                    if(line.isEmpty() || line.startsWith("#") || !hasTextSection || labels.containsKey(line)) continue;

                    converter.new_instruction(line);
                    writer.write(converter.instruction_to_hex());
                    writer.newLine();
                    address += 0x00000004;
                }

        } catch(IOException io){
            System.out.println(io.getMessage() + "\n\n" + io.getCause());
        }
    }

    private static HashMap<String, Integer> label_determiner(String inFile){
        HashMap<String, Integer> labels = new HashMap<>();
        try(
                BufferedReader reader = new BufferedReader(new FileReader(inFile))
                ){
            InstructionConverter converter = new InstructionConverter(inFile);
            int address = 0x00400000;
            String line;
            Pattern pattern = Pattern.compile("\\b\\w+:");
            boolean hasTextSection = false;

            while((line = reader.readLine()) != null){
                line = line.trim();

                if(line.contains(".text")){hasTextSection = true; continue;}
                if(line.isEmpty() || line.startsWith("#") || !hasTextSection) continue;

                Matcher matcher = pattern.matcher(line);

                if(matcher.matches()){
                    labels.put(matcher.group().substring(0, matcher.end()), address);
                    continue;
                }

                converter.new_instruction(line);
                if(converter.instruction_to_hex().contains("\n")) address += 8;
                else address += 4;
            }
        } catch(IOException io){
            System.out.println(io.getMessage() + "\n\n" + io.getCause());
        }
        return labels;
    }
}
