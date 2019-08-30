
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * WRITE byte code: output the value at top of RunTimeStack to standard output and return that value.
 */
public class WriteCode extends ByteCode {

    private int value;

    public void setValue(int newValue) {
        this.value = newValue;
    }

    public int getValue() {
        return this.value;
    }

    public void init(ArrayList<String> args) {
    }

    public void print(VirtualMachine vm) {
        System.out.println("WRITE");
    }

    public void execute(VirtualMachine vm) {
        value = vm.write();
    }

}
