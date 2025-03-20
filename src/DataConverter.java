import java.io.*;
import java.util.regex.*;

public class DataConverter {

    // convert string to hex representation
    private static String stringToHex(String data) {
        StringBuilder hexstring = new StringBuilder();
        for(char c : data.toCharArray()) {
            hexstring.append(String.format("%08x", (int) c)).append(" ");
        }
        return hexstring.toString().trim();
    }

    // Convert hex string to little endian
    private static String convertLittleEndian(String data) {
        // Split the string into two-character chunks
        StringBuilder littleEndianHex = new StringBuilder();
        for (int i = 0; i < data.length(); i += 2) {
            // Handle case where the length is odd
            String chunk = data.substring(i, Math.min(i + 2, data.length()));
            // Convert to hex
            String hexData = stringToHex(chunk);
            // Reverse the chunk
            littleEndianHex.insert(0, hexData + " ");
        }
        return littleEndianHex.toString().trim();
    }

    // Process the ASM file and convert to .data format
    public static int[] processAsmFile(String inputFile) {
        // Generate the output file name by replacing .asm with .data
        String outputFile = inputFile.replaceAll("\\.asm$", "\\.data");
        int currentAddress = 0x10010000; // Starting address
        int[] addresses = new int[100];

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            boolean dataSectionFound = false;
            int lineNumber = 1;
            int addressIndex = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Look for the start of the data section
                if (line.contains(".data")) {
                    dataSectionFound = true;
                    continue;  // Skip the .data line
                }

                // If the data section is found, process data lines
                if (dataSectionFound) {
                    Pattern pattern = Pattern.compile("^\\s*(\\w+)\\s+db\\s+\"(.*)\"$");
                    Matcher matcher = pattern.matcher(line);

                    if (matcher.matches()) {
                        String dataLabel = matcher.group(1);
                        String dataValue = matcher.group(2);

                        // Convert the data to little-endian
                        String littleEndianHex = convertLittleEndian(dataValue);

                        int dataLength = dataValue.length();

                        // Store address
                        addresses[addressIndex] = currentAddress;


                        // Write the line number and the little-endian hex data to the output file
                        bw.write(String.format("%04d %s%n", lineNumber, littleEndianHex));
                        lineNumber++;

                        currentAddress += dataLength;
                        addressIndex++;
                    }
                }

                // If we encounter a new section stop processing
                if (line.startsWith(".text")) {
                    break;
                }
            }

            System.out.println("Data section has been converted and saved to " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }
}
