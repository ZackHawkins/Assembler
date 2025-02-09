import java.util.HashMap;
public class Main {
    public static void main(String[] args) {
        Converter op = new Converter("addi $sp, $sp, 4");
    }
}




class Converter {

    private final HashMap<String, Integer> mnemonic = new HashMap<>();

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

    private String instruction; //instruction from the command line

    /**
     * zero parameter constructor that calls the specifying constructor and
     * sets the default instruction variable to null
     */
    public Converter(){
        this(null);
    }

    /**
     * specifying constructor sets the instruction variable from what was passed
     * in as a string
     * @param instruction
     */
    public Converter(String instruction){
        this.instruction = instruction;
        load_mnemonic();
        System.out.println(this.instruction);
    }

    private void load_mnemonic(){
        mnemonic.put("add", 0);

    }

}