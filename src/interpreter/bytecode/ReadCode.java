
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * READ byte code: prompts user to enter an integer, pushes it to RunTimeStack
 */

public class ReadCode extends ByteCode {
    private int value;

    public void init(ArrayList<String> args) {
    }

    public void print(VirtualMachine vm) {
        System.out.println("READ");
    }

    public void execute(VirtualMachine vm) {
        value = vm.read();
    }

}
