import java.util.*;
import java.io.*;

public class TextConverter {

    private int address;
    private Converter converter;
    private HashMap<String, Integer> data;

    public TextConverter(String inFile){
        this.converter = new Converter(inFile);
        this.data = converter.get_data_information();
        this.address = 0x00400000;
    }






}
