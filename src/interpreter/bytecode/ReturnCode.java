
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * RETURN byte code: return from the current function to the instruction
 * after the associated call instruction
 */

public class ReturnCode extends ByteCode {
    String label = null;
    int returnValue;

    public void init(ArrayList<String> args) {
        if (args.size() == 1) {
            label = args.get(0);
        }
    }

    /*
     * If dumping is on, print the Return code in a formatting appropriate
     * for what is being returned from and if a base-id was specified
     */
    public void print(VirtualMachine vm) {
        System.out.print("RETURN ");
        if (label != null && label.contains("<<")) {
            System.out.print(label + "   ");
            // get the base id, ignoring <<>> in label
            int indexOfBrackets = label.indexOf("<<");
            String baseID = label.substring(0, indexOfBrackets);
            System.out.print("exit " + baseID + ": " + returnValue);
        } else if (label != null) {
            System.out.print(label + "   exit: " + returnValue);

        } else {
            // account for RETURN from a READ or WRITE execution
            if (vm.getReadWrite() == 1) {
                System.out.print("     exit READ: " + returnValue);
                resetReadWriteFlag(vm);

            }
            if (vm.getReadWrite() == 2) {
                System.out.print("     exit WRITE: " + returnValue);
                resetReadWriteFlag(vm);
            }
        }
        System.out.print("\n");
    }

    public void execute(VirtualMachine vm) {
        returnValue = vm.returnAfterCall();
        // for dumping, raise the appropriate read/write flag in the vm
        if (vm.getReadWrite() != 0) vm.setReadWrite(0);
    }

    private void resetReadWriteFlag(VirtualMachine vm) {
        vm.setReadWrite(0);
    }

    public String getLabel() {
        return label;
    }

    public void setReturnValue(int value) {
        returnValue = value;
    }
}
