
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * STORE byte code: pop the top of the RunTimeStack and store the value in the
 * position of the indicated offset from the start of the current frame
 */

public class StoreCode extends ByteCode {
    private int offsetValue;
    private String name = null;
    private int storedValue;

    public void init(ArrayList<String> args) {
        offsetValue = Integer.parseInt(args.get(0));
        if (args.size() == 2) {
            name = args.get(1);
        }
    }

    public void print(VirtualMachine vm) {
        System.out.print("STORE " + offsetValue);
        if (name != null) {
            System.out.print(" " + name);
            System.out.print("    " + name + " = " + storedValue);
        }
        System.out.print("\n");
    }

    public void execute(VirtualMachine vm) {
        storedValue = vm.storeRunStack(offsetValue);

    }

}
