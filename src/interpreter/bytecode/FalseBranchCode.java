
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * FALSEBRANCH byte code: examines top of stack; if 0, program execution jumps
 * to the instruction indicated by the private variable target
 */

public class FalseBranchCode extends ByteCode {
    private String label;
    private int target;
    
    public void init(ArrayList<String> args) {
        label = args.get(0);
    }
    
    public void print(VirtualMachine vm) {
        System.out.println("FALSEBRANCH " + label);
    }

    public void execute(VirtualMachine vm) {
        int topOfStack = vm.popRunStack();
        if (topOfStack==0) {
            /*
             * vm.setPC requests that VM change the pc; the "-1" is because the VM will
             * increment the pc after execution, so for the next execution, the
             * -1 here ensures that it executes the desired code
             */
            vm.setPC(target-1);
        }
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setTarget(int position) {
        target = position;
    }
    
} // end class
