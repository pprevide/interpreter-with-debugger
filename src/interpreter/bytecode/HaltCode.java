
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * HALT byte code: stops execution of program.
 * Upon executing this code, the virtual machine stops running.
 */
public class HaltCode extends ByteCode {

    public void init(ArrayList<String> args) {
    }

    public void print(VirtualMachine vm) {
        System.out.println("HALT");
    }

    public void execute(VirtualMachine vm) {
        vm.setIsRunning(false);
    }


}
