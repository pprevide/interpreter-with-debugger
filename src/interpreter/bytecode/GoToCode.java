
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * GOTO byte code: jumps to the indicated instruction in the program.
 * The instruction to which to jump is indicated by the integer field target.
 */

public class GoToCode extends ByteCode {
    
    private String label;
    private int target;
    
    public void init(ArrayList<String> args) {
        label = args.get(0);
    }
    
    public void print(VirtualMachine vm) {
        System.out.println("GOTO " + label);
    }
    
    /*
     * Request that VM change the pc; the "-1" is because the VM will 
     * increment the pc after execution, so for the next execution, the
     * -1 here ensures that it executes the desired code
     */
    public void execute(VirtualMachine vm) {
        vm.setPC(target-1);
    }
    
    public void setTarget(int i) {
        target = i;
    }
    
    public String getLabel() {
        return label;
    }
    
    
    
    
    
    
    
}
