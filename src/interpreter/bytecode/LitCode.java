
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * LitCode byte code pushes the indicated integer to the top of the virtual machine's runtime stack.
 */
public class LitCode extends ByteCode {

    private int value;
    private String name = null;

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setValue(int newValue) {
        value = newValue;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void init(ArrayList<String> args) {
        if (args.size() == 1) {
            value = Integer.parseInt(args.get(0));
        } else {
            value = 0;
            name = args.get(1);
        }
    }

    public void print(VirtualMachine vm) {
        System.out.print("LIT " + value);
        if (name != null) {
            System.out.print(" " + name + "     int " + name);
        }
        System.out.print("\n");
    }

    public void execute(VirtualMachine vm) {
        vm.pushRunStack(value);
    }

}
