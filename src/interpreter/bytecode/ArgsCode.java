
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * ARGS byte code: create a new frame starting n elements from the top of the
 * RunTimeStack.  These n elements are the arguments to be made available in the new
 * frame, which represents a function call. 
 */
public class ArgsCode extends ByteCode {

    private int numberArgs;

    public void init(ArrayList<String> args) {
        numberArgs = Integer.parseInt(args.get(0));
    }

    public void print(VirtualMachine vm) {
        System.out.println("ARGS " + numberArgs);
    }

    public void execute(VirtualMachine vm) {
        vm.createArgsFrame(numberArgs);
    }

}
