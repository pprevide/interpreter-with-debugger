
package interpreter;

import java.util.ArrayList;

/**
 * This is an abstract class for byte code instances, and it contains 
 * no data fields that relate to arguments, per the instructions.  
 * The instances of the concrete classes will be responsible for processing
 * their arguments when ByteCodeLoader executes.
 * Each method below is implemented by the concrete classes.
 * The byte codes are an example of the concept of polymorphism.
 */

public abstract class ByteCode {
    // Initializes the arguments of the particular bytecode from an ArrayList
    public abstract void init(ArrayList<String> args);
    /* Requests that the VM carrry out the instructions of that bytecode
     * during execution of the program*/
    public abstract void execute(VirtualMachine vm);
    // Prints the bytecode appropriately if DUMP is ON
    public abstract void print(VirtualMachine vm);   
}
