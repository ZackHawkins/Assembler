import java.io.*;
import java.util.HashMap;
import java.util.regex.*;

public class DataConverter {

    // Convert a string into an array of bytes
    private static byte[] stringToBytes(String data) {
        byte[] byteArray = new byte[data.length() + 1];
        for (int i = 0; i < data.length(); i++) {
            byteArray[i] = (byte) data.charAt(i);
        }
        return byteArray;
    }

    // Convert a byte array to little endian formatted string of hexadecimal
    private static String convertLittleEndian(byte[] byteArray) {
        StringBuilder littleEndianHex = new StringBuilder();

        for (byte b: byteArray) {
            // Convert each byte to hexadecimal
            littleEndianHex.append(String.format("%02x", b));
        }

        String hexData = littleEndianHex.toString();
        StringBuilder littleEndianResult = new StringBuilder();

        for (int i = 0; i < hexData.length(); i += 8) {
            // Break the data into chunks so each chunk is 4 bytes and process it that way
            String chunk = hexData.substring(i, Math.min(i + 8, hexData.length()));

            // For padding last chunk if it's not 4 bytes
            if (chunk.length() < 8) {
                chunk = String.format("%-8s", chunk).replace(' ', '0');
            }

            // Reverse chunk and append it to the result
            for (int j = 3; j >= 0; j--) {
                littleEndianResult.append(chunk.substring(j * 2, j * 2 + 2));
            }
        }
        return littleEndianResult.toString();
    }

    // Converts a hex string into a byte array, this is used to convert the large string
    // of combined data into a byte array to be used in little endian conversion
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    // Process the ASM file and convert to .data format
    public static HashMap<String, Integer> processAsmFile(String inputFile) {
        // Generate the output file name by replacing .asm with .data
        String outputFile = inputFile.replaceAll("\\.asm$", "\\.data");
        int currentAddress = 0x10010000; // Starting address of data to use in hash map for .text portion
        HashMap<String, Integer> addresses = new HashMap<>();
        StringBuilder combinedData = new StringBuilder(); // Have to combine data in order to process correctly
        // it keeps getting messed up if we go line by line

        // Create file reader and writer
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;

            // if we reach the data section
            boolean dataSection = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // We want to skip any lines with spaces, comments, or tabs
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("\t")) {
                    continue;
                }

                // Look for the start of the data section
                if (line.contains(".data")) {
                    dataSection = true;
                    continue;  // Skip the .data line
                }

                // If we reach the data section, process data lines
                if (dataSection) {
                    Pattern pattern = Pattern.compile("^\\s*(\\w+):\\s+\\.asciiz\\s+\"(.*)\"$");
                    Matcher matcher = pattern.matcher(line);

                    if (matcher.matches()) {
                        // this matches to the label of the data
                        String dataLabel = matcher.group(1);

                        // this matches to the piece of data, anything in quotes
                        String dataValue = matcher.group(2);

                        // Convert the data piece from a string to a byte array to be processed
                        // This takes all the data pieces and combines them into one byte array
                        byte[] byteArray = stringToBytes(dataValue);
                        for (byte b : byteArray) {
                            combinedData.append(String.format("%02x", b)); // Convert bytes to hex and append
                        }

                        // The length of the data piece, this is used to calculate the address for
                        // the piece of data being stored in the hashmap
                        int dataLength = dataValue.length() + 1;
                      
                        // Store address of the data piece along with it's label
                        addresses.put(dataLabel, currentAddress);

                        // Increment the current address by the length of the data
                        currentAddress += dataLength;
                    }
                }

                // If we encounter a new section stop processing
                if (line.startsWith(".text")) {
                    break;
                }
            }

            // Converts the combined data into little endian format, this will be one large hexadecimal
            String littleEndianHex = convertLittleEndian(hexStringToByteArray(combinedData.toString()));

            // Then we chunk the data and write it to the .data file, the data is chunked into 4 bytes
            int index = 0;
            while (index < littleEndianHex.length()) {
                String chunk = littleEndianHex.substring(index, Math.min(index + 8, littleEndianHex.length()));
                bw.write(String.format("%s%n", chunk));
                index += 8;
            }


            System.out.println("Data section has been converted and saved to " + outputFile.substring(outputFile.lastIndexOf('/')+1));


        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return a hashmap of the addresses each piece of data is at, the key is the label for the data
        return addresses;
    }
}
