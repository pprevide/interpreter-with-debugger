
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * CALL byte code: begins execution of a function and causes program control to 
 * transfer to the function.
 * This byte code pushes the current instruction to the address stack, and
 * sets the program counter to the first instruction of the function to be executed.
 */

public class CallCode extends ByteCode {
    private String label=null;
    private int target;
    
    public String getLabel() {
        return label;
    }
    
    public void setTarget (int newTarget) {
        target = newTarget;
    }
    
    public int getTarget() {
        return target;
    }
    
    
    public void init(ArrayList<String> args) {
        label = args.get(0);
    }
   
    /*
     * The output of the print method is formatted appropriately per 
     * the instructions, depending on the Call.
     */
    public void print(VirtualMachine vm) {
        System.out.print("CALL " + label + "   ");
        if (label.contains("<<")) {
            int indexOfBrackets = label.indexOf("<<");
            String baseID = label.substring(0,indexOfBrackets);
            System.out.println(baseID +""+ vm.topFrameAsString() );
        }
        else if (label.equals("Read")) {
            System.out.println("Read()");
        }
        else if (label.equals("Write")) {
            System.out.println("Write()");
        }
        else {System.out.print("\n");}
    }
    
    public void execute(VirtualMachine vm) {
        // push the current instruction address to the addrsStack in vm
        vm.pushAddrsStack();
        
        /*
         * Request that VM change the pc; the "-1" is because the VM will 
         * increment the program counter after execution, so for the next execution, the
         * -1 here ensures that it executes the desired code
         */
        vm.setPC(target-1); 
        
    }
    
}
