
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * LABEL byte code: indicates a destination for various types of jumps, such as
 * GoTo, Call, or the FalseBranch codes
 */

public class LabelCode extends ByteCode {

    private String label = null;

    public void init(ArrayList<String> args) {
        label = args.get(0);
    }

    public void print(VirtualMachine vm) {
        System.out.println("LABEL " + label);
    }

    /*
     * If the label corresponds to Read or Write, request that the VM set the 
     * readWrite flag appropriately so that the dump output will be 
     * appropriately formatted upon returning from the Read or Write calls
     */
    public void execute(VirtualMachine vm) {

        // set the flag associated with read or write codes
        if (label.equals("Read")) {
            vm.setReadWrite(1);
        }
        if (label.equals("Write")) {
            vm.setReadWrite(2);
        }
    }

    public String getLabel() {
        return label;
    }


}
